package uy.com.fing.hicscan.hceanalysis.usecases;

import org.springframework.stereotype.Service;

import uy.com.fing.hicscan.hceanalysis.data.OntoForms.OntoForms;
import uy.com.fing.hicscan.hceanalysis.data.OntoForms.datatypes.OntoTree;
import uy.com.fing.hicscan.hceanalysis.data.OntoForms.datatypes.Form;
import uy.com.fing.hicscan.hceanalysis.data.OntoForms.datatypes.PropertyDescriptor;
import uy.com.fing.hicscan.hceanalysis.data.OntoForms.datatypes.PropertyDescriptorWithFormStatus;
import uy.com.fing.hicscan.hceanalysis.data.OntoForms.datatypes.CalculatedPropertyConfigDescriptor;
import uy.com.fing.hicscan.hceanalysis.data.OntoForms.datatypes.ArtificeClassConfigDescriptor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Collections;

@Service
public class ConfigurationUseCase {
    
    private final OntoForms ontoForms;
    
    public ConfigurationUseCase(OntoForms ontoForms) {
        this.ontoForms = ontoForms;
    }
    
    public String[] getOntologies() {
        try {
            return ontoForms.getOntologyNames();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error retrieving ontologies", e);
        }
    }
    
    public String createOntology(String fileName, byte[] fileContent, String ontologyName) {
        try {
            // Replace spaces with underscores in ontology name
            String sanitizedOntologyName = ontologyName.replace(" ", "_");
            
            // Create a temporary file to pass to OntoForms using sanitized ontology name
            File tempFile = createTempFile(fileName, fileContent, sanitizedOntologyName);
            
            // Upload the ontology using OntoForms and get the returned URL
            String ontologyUrl = ontoForms.uploadOntology(tempFile);
            
            // Clean up the temporary file
            tempFile.delete();
            
            // Create app mapping after successful upload using sanitized ontologyName with file extension
            String extension = getFileExtension(fileName);
            String ontologyNameWithExtension = sanitizedOntologyName + extension;
            ontoForms.postAppMapping(ontologyNameWithExtension, sanitizedOntologyName);
            
            // Return the ontology URL from the upload
            return ontologyUrl;
            
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error processing ontology file", e);
        }
    }
    
    public OntoTree getOntologyClasses(String ontoId) {
        try {
            return ontoForms.getOntologyClasses(ontoId);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error retrieving ontology classes", e);
        }
    }
    
    // Returns a list of properties that are in the form and their status (shown or hidden)
    public List<PropertyDescriptorWithFormStatus> getFormToModify(String ontoId, String classUri) {
        return getFormToModifyInternal(ontoId, classUri, new HashMap<>());
    }
    
    // Internal method to get the form to modify
    private List<PropertyDescriptorWithFormStatus> getFormToModifyInternal(String ontoId, String classUri, Map<String, List<PropertyDescriptorWithFormStatus>> cache) {
        try {
            // Get the form for the class
            Form form = ontoForms.getOntologyClassForm(ontoId, classUri);
            if (form != null) {
            }
            
            // Get all properties for the class
            List<PropertyDescriptor> allProperties = ontoForms.getOntologyClassProperties(ontoId, classUri);
            System.out.println("All properties count: " + allProperties.size());
            
            // Get all classes from the ontology to check if ranges match class names
            Set<String> allClassNames = new HashSet<>();
            Map<String, String> classNameToUriMap = new HashMap<>();
            try {
                OntoTree ontoTree = ontoForms.getOntologyClasses(ontoId);
                if (ontoTree != null) {
                    collectClassNamesAndUris(ontoTree, allClassNames, classNameToUriMap);
                }
            } catch (Exception e) {
                System.out.println("Warning: Could not retrieve class names for range checking: " + e.getMessage());
            }
            
            // Create a map of property URIs to their corresponding form fields for options access
            Map<String, Form.FormField> formFieldMap = new HashMap<>();
            Set<String> formPropertyUris = new HashSet<>();
            if (form != null && form.getFields() != null) {
                for (Form.FormField field : form.getFields()) {
                    if (field.getUri() != null) {
                        formFieldMap.put(field.getUri(), field);
                        formPropertyUris.add(field.getUri());
                    }
                }
            }
            
            // Create the result list with form status, options, and subForm for functional properties
            List<PropertyDescriptorWithFormStatus> result = new ArrayList<>();
            for (PropertyDescriptor property : allProperties) {
                boolean isShown = formPropertyUris.contains(property.getPropUri());
                List<Form.FieldOption> options = null;
                List<PropertyDescriptorWithFormStatus> subForm = null;
                
                // Get options from form field if it exists (even for hidden properties)
                Form.FormField formField = formFieldMap.get(property.getPropUri());
                if (formField instanceof Form.ObjectField) {
                    Form.ObjectField objectField = (Form.ObjectField) formField;
                    options = objectField.getOptions();
                }
                
                // Determine if the property can be transparented
                boolean canBeTransparented = false;
                if (property.getRange() != null) {
                    // Check if the range equals a class name
                    if (allClassNames.contains(property.getRange())) {
                        String rangeClassUri = classNameToUriMap.get(property.getRange());
                        if (rangeClassUri != null) {
                            try {
                                // Get properties for the range class
                                List<PropertyDescriptor> rangeClassProperties = ontoForms.getOntologyClassProperties(ontoId, rangeClassUri);
                                // If the class has properties, it can be transparented
                                canBeTransparented = !rangeClassProperties.isEmpty();
                            } catch (Exception e) {
                                System.out.println("Warning: Could not retrieve properties for range class " + property.getRange() + ": " + e.getMessage());
                                canBeTransparented = false;
                            }
                        }
                    }
                }
                
                // Check if this property has a range and if it matches a subForm sectionName (only if it can be transparented)
                if (property.getRange() != null && canBeTransparented) {
                    // Check if the form has subForms and if any subForm's sectionName matches the range
                    if (form != null && form.getSubForms() != null) {
                        // Sort subforms for consistent ordering
                        List<Form> sortedSubForms = sortSubForms(form.getSubForms());
                        for (Form subFormItem : sortedSubForms) {
                            if (subFormItem.getSectionName() != null && subFormItem.getSectionName().equals(property.getRange())) {
                                try {
                                    // Convert the subForm to List<PropertyDescriptorWithFormStatus>
                                    subForm = convertFormToPropertyDescriptorWithFormStatus(ontoId, subFormItem);
                                    break; // Found the matching subForm, no need to continue searching
                                } catch (Exception e) {
                                    System.out.println("Warning: Could not retrieve subForm for property " + property.getPropLabel() + ": " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        System.out.println("No subForms available or form is null");
                    }
                } else {
                    if (property.getRange() == null) {
                        System.out.println("Property " + property.getPropLabel() + " has no range");
                    } else if (!canBeTransparented) {
                        System.out.println("Property " + property.getPropLabel() + " cannot be transparented (range: " + property.getRange() + ")");
                    }
                }
                
                result.add(new PropertyDescriptorWithFormStatus(property, isShown, options, subForm, canBeTransparented));
            }
                      
            return result;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error retrieving form and properties for modification", e);
        }
    }
    
    // Helper method to convert a Form object to List<PropertyDescriptorWithFormStatus>
    private List<PropertyDescriptorWithFormStatus> convertFormToPropertyDescriptorWithFormStatus(String ontoId, Form form) {
        return convertFormToPropertyDescriptorWithFormStatus(ontoId, form, new HashSet<>());
    }
    
    // Helper method to convert a Form object to List<PropertyDescriptorWithFormStatus> with cycle detection
    private List<PropertyDescriptorWithFormStatus> convertFormToPropertyDescriptorWithFormStatus(String ontoId, Form form, Set<String> processedClassUris) {
        if (form == null) {
            return new ArrayList<>();
        }
        
        // Prevent infinite loops by checking if we've already processed this class URI
        if (processedClassUris.contains(form.getClassUri())) {
            return new ArrayList<>();
        }
        
        // Add current class URI to processed set
        processedClassUris.add(form.getClassUri());
        
        try {
            // Create the result list
            List<PropertyDescriptorWithFormStatus> result = new ArrayList<>();
            
            if (form.getSubForms() != null && !form.getSubForms().isEmpty()) {
                // Instead of processing the current form, go directly to fetch the form for this class
                Form actualForm = ontoForms.getOntologyClassForm(ontoId, form.getClassUri());
                
                if (actualForm != null) {
                    // Process the actual form directly and add it to the result
                    
                    // Create a map of property URIs to their corresponding form fields for options access
                    Map<String, Form.FormField> actualFormFieldMap = new HashMap<>();
                    Set<String> actualFormPropertyUris = new HashSet<>();
                    if (actualForm.getFields() != null) {
                        for (Form.FormField field : actualForm.getFields()) {
                            if (field.getUri() != null) {
                                actualFormFieldMap.put(field.getUri(), field);
                                actualFormPropertyUris.add(field.getUri());
                            }
                        }
                    }
                    
                    // Get properties for the actual form's class
                    // List<PropertyDescriptor> actualFormProperties = ontoForms.getOntologyClassProperties(ontoId, actualForm.getClassUri());
                    
                    // Process each property from the actual form
                    // for (PropertyDescriptor property : actualFormProperties) {
                    //     boolean isShown = actualFormPropertyUris.contains(property.getPropUri());
                    //     List<Form.FieldOption> options = null;
                    //     List<PropertyDescriptorWithFormStatus> subForm = null;
                    //     boolean canBeTransparented = false;
                        
                    //     // Get options from form field if it exists
                    //     Form.FormField formField = actualFormFieldMap.get(property.getPropUri());
                    //     if (formField instanceof Form.ObjectField) {
                    //         Form.ObjectField objectField = (Form.ObjectField) formField;
                    //         options = objectField.getOptions();
                    //     }
                        
                    //     result.add(new PropertyDescriptorWithFormStatus(property, isShown, options, subForm, canBeTransparented));
                    // }
                } else {
                    System.out.println("No actual form found for class " + form.getClassUri() + ", adding empty result");
                }
            } else {
                // Get all properties for the form's class URI
                List<PropertyDescriptor> allProperties = ontoForms.getOntologyClassProperties(ontoId, form.getClassUri());
                
                // Create a map of property URIs to their corresponding form fields for options access
                Map<String, Form.FormField> formFieldMap = new HashMap<>();
                Set<String> formPropertyUris = new HashSet<>();
                if (form.getFields() != null) {
                    for (Form.FormField field : form.getFields()) {
                        if (field.getUri() != null) {
                            formFieldMap.put(field.getUri(), field);
                            formPropertyUris.add(field.getUri());
                        }
                    }
                }
                
                // Get all classes from the ontology to check if ranges match class names
                Set<String> allClassNames = new HashSet<>();
                Map<String, String> classNameToUriMap = new HashMap<>();
                try {
                    OntoTree ontoTree = ontoForms.getOntologyClasses(ontoId);
                    if (ontoTree != null) {
                        collectClassNamesAndUris(ontoTree, allClassNames, classNameToUriMap);
                    }
                } catch (Exception e) {
                    System.out.println("Warning: Could not retrieve class names for range checking: " + e.getMessage());
                }
                
                for (PropertyDescriptor property : allProperties) {
                    boolean isShown = formPropertyUris.contains(property.getPropUri());
                    List<Form.FieldOption> options = null;
                    List<PropertyDescriptorWithFormStatus> subForm = null;
                    boolean canBeTransparented = false;
                    
                    // Get options from form field if it exists
                    Form.FormField formField = formFieldMap.get(property.getPropUri());
                    if (formField instanceof Form.ObjectField) {
                        Form.ObjectField objectField = (Form.ObjectField) formField;
                        options = objectField.getOptions();
                    }
                    
                    // Determine if the property can be transparented
                    if (property.getRange() != null) {
                        // Check if the range equals a class name
                        if (allClassNames.contains(property.getRange())) {
                            String rangeClassUri = classNameToUriMap.get(property.getRange());
                            if (rangeClassUri != null) {
                                try {
                                    // Get properties for the range class
                                    List<PropertyDescriptor> rangeClassProperties = ontoForms.getOntologyClassProperties(ontoId, rangeClassUri);
                                    // If the class has properties, it can be transparented
                                    canBeTransparented = !rangeClassProperties.isEmpty();
                                } catch (Exception e) {
                                    canBeTransparented = false;
                                }
                            }
                        }
                    }
                    
                    // Check if this property has a range and if it matches a subForm sectionName (only if it can be transparented)
                    if (property.getRange() != null && canBeTransparented && form.getSubForms() != null) {
                        // Sort subforms for consistent ordering
                        List<Form> sortedSubForms = sortSubForms(form.getSubForms());
                        for (Form subFormItem : sortedSubForms) {
                            if (subFormItem.getSectionName() != null && subFormItem.getSectionName().equals(property.getRange())) {
                                try {
                                    // Recursively convert the subForm
                                    subForm = convertFormToPropertyDescriptorWithFormStatus(ontoId, subFormItem, new HashSet<>(processedClassUris));
                                    break; // Found the matching subForm, no need to continue searching
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    
                    result.add(new PropertyDescriptorWithFormStatus(property, isShown, options, subForm, canBeTransparented));
                }
            }

            // After processing all properties, also process all available subforms maintaining their structure
            if (form.getSubForms() != null && !form.getSubForms().isEmpty()) {
                // Sort subforms for consistent ordering
                List<Form> sortedSubForms = sortSubForms(form.getSubForms());
                for (Form subFormItem : sortedSubForms) {
                    try {
                        // Create a property descriptor for this subform section
                        PropertyDescriptor subFormProperty = new PropertyDescriptor();
                        subFormProperty.setPropLabel(subFormItem.getSectionName()); // Use sectionName as propLabel
                        subFormProperty.setPropUri(subFormItem.getClassUri()); // Use classUri as propUri
                        subFormProperty.setDomain(form.getClassUri());
                        subFormProperty.setRange(subFormItem.getSectionName());
                        subFormProperty.setPropType("object");
                        subFormProperty.setFunctional(false);
                        subFormProperty.setInverseFunctional(false);
                        
                        // Convert the subform recursively
                        List<PropertyDescriptorWithFormStatus> convertedSubForm = convertFormToPropertyDescriptorWithFormStatus(ontoId, subFormItem, new HashSet<>(processedClassUris));
                        
                        if (convertedSubForm != null && !convertedSubForm.isEmpty()) {
                            
                            // Create PropertyDescriptorWithFormStatus for this subform section
                            PropertyDescriptorWithFormStatus subFormPropertyWithStatus = new PropertyDescriptorWithFormStatus(
                                subFormProperty, 
                                true, // isShown
                                null, // options
                                convertedSubForm, // subForm - this maintains the hierarchical structure
                                true // canBeTransparented
                            );
                            
                            result.add(subFormPropertyWithStatus);
                        }
                    } catch (Exception e) {
                        System.out.println("Warning: Could not process subform " + subFormItem.getSectionName() + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                
            }
            
            return result;
        } catch (IOException | InterruptedException e) {
            return new ArrayList<>();
        } finally {
            // Remove current class URI from processed set when done
            processedClassUris.remove(form.getClassUri());
        }
    }
    
    // Helper method to sort subforms consistently by sectionName
    private List<Form> sortSubForms(List<Form> subForms) {
        if (subForms == null || subForms.isEmpty()) {
            return subForms;
        }
        
        List<Form> sortedSubForms = new ArrayList<>(subForms);
        Collections.sort(sortedSubForms, new Comparator<Form>() {
            @Override
            public int compare(Form f1, Form f2) {
                String sectionName1 = f1.getSectionName();
                String sectionName2 = f2.getSectionName();
                
                // Handle null section names
                if (sectionName1 == null && sectionName2 == null) {
                    return 0;
                }
                if (sectionName1 == null) {
                    return 1; // null goes to end
                }
                if (sectionName2 == null) {
                    return -1; // null goes to end
                }
                
                // Sort alphabetically by section name for consistent ordering
                return sectionName1.compareTo(sectionName2);
            }
        });
        
        return sortedSubForms;
    }
    
    // Helper method to recursively collect all class names and URIs from the OntoTree
    private void collectClassNamesAndUris(OntoTree ontoTree, Set<String> classNames, Map<String, String> classNameToUriMap) {
        if (ontoTree == null) return;
        
        // Add the current node's class name and URI if they exist
        if (ontoTree.getData() != null && ontoTree.getData().className() != null) {
            String className = ontoTree.getData().className();
            String uri = ontoTree.getData().uri();
            classNames.add(className);
            if (uri != null) {
                classNameToUriMap.put(className, uri);
            }
        }
        
        // Recursively process all children
        if (ontoTree.getChildren() != null) {
            for (OntoTree child : ontoTree.getChildren()) {
                collectClassNamesAndUris(child, classNames, classNameToUriMap);
            }
        }
    }
    
    private File createTempFile(String fileName, byte[] fileContent, String sanitizedOntologyName) throws IOException {
        // Create a temporary file with the sanitized ontology name and original filename extension
        String extension = getFileExtension(fileName);
        // Note: sanitizedOntologyName is already sanitized by the caller
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File tempFile = new File(tempDir, sanitizedOntologyName + extension);
        
        // Write the content to the temporary file
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(fileContent);
        }
        
        return tempFile;
    }
    
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return ".tmp";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
    
    public void postFormCalculatedPropertyConfig(String ontoId, CalculatedPropertyConfigDescriptor calculatedPropertyConfigDescriptor) {
        try {
            ontoForms.postOntologyCalculatedPropertyConfig(ontoId, calculatedPropertyConfigDescriptor);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error posting calculated property configuration", e);
        }
    }
    
    public void deleteFormCalculatedPropertyConfig(String ontoId, CalculatedPropertyConfigDescriptor calculatedPropertyConfigDescriptor) {
        try {
            ontoForms.deleteOntologyCalculatedPropertyConfig(ontoId, calculatedPropertyConfigDescriptor);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error deleting calculated property configuration", e);
        }
    }
    
    public void postFormArtificeClassConfig(String ontoId, ArtificeClassConfigDescriptor artificeClassConfigDescriptor) {
        try {
            ontoForms.postOntologyArtificeClassConfig(ontoId, artificeClassConfigDescriptor);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error posting artifice class configuration", e);
        }
    }
    
    public void deleteFormArtificeClassConfig(String ontoId, ArtificeClassConfigDescriptor artificeClassConfigDescriptor) {
        try {
            ontoForms.deleteOntologyArtificeClassConfig(ontoId, artificeClassConfigDescriptor);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error deleting artifice class configuration", e);
        }
    }
    
    /**
     * Gets a form filtered by risk model questions and their options
     * @param ontoId The ontology ID
     * @param classUri The URI of the class
     * @param riskModelUri The URI of the risk model
     * @return List of property descriptors with filtered form status
     */
    public List<PropertyDescriptorWithFormStatus> getRiskModelFilteredForm(String ontoId, String classUri, String riskModelUri) {
        try {
            // Get the base form for the specified class (same as getFormToModify)
            List<PropertyDescriptorWithFormStatus> baseForm = getFormToModify(ontoId, classUri);
            
            // Filter the form based on risk model questions and their options
            return filterFormByRiskModelQuestions(baseForm, ontoId, riskModelUri);
            
        } catch (Exception e) {
            throw new RuntimeException("Error getting risk model filtered form", e);
        }
    }
    
    /**
     * Filters the form based on risk model questions and their options
     * @param baseForm The base form to filter
     * @param ontoId The ontology ID
     * @param riskModelUri The URI of the risk model
     * @return Filtered form
     */
    private List<PropertyDescriptorWithFormStatus> filterFormByRiskModelQuestions(
            List<PropertyDescriptorWithFormStatus> baseForm, 
            String ontoId, 
            String riskModelUri) {
        
        // For each property in the form, filter its options
        for (PropertyDescriptorWithFormStatus property : baseForm) {
            if (property.getOptions() != null) {
                // Filter OUT options that start with ACS, Gail, or IBIS (remove them)
                List<Form.FieldOption> filteredOptions = property.getOptions().stream()
                        .filter(option -> {
                            String label = option.getLabel();
                            return label != null && !(label.startsWith("ACS") || label.startsWith("Gail") || label.startsWith("IBIS"));
                        })
                        .toList();
                
                // Update the property with filtered options
                property.setOptions(filteredOptions);
            }
        }
        
        return baseForm;
    }
}
