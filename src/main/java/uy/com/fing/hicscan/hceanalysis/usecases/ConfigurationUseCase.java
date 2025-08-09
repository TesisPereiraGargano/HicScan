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
            System.out.println("Form retrieved: " + (form != null ? "not null" : "null"));
            if (form != null) {
                System.out.println("Form fields count: " + (form.getFields() != null ? form.getFields().size() : "null"));
                if (form.getFields() != null && form.getFields().isEmpty()) {
                    System.out.println("Empty form returned for class " + classUri + " - no form configuration exists");
                }
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
                    System.out.println("Collected class names: " + allClassNames);
                    System.out.println("Class name to URI map: " + classNameToUriMap);
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
                        System.out.println("Added form field URI: " + field.getUri());
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
                                System.out.println("Property " + property.getPropLabel() + " canBeTransparented: " + canBeTransparented + " (range class has " + rangeClassProperties.size() + " properties)");
                            } catch (Exception e) {
                                System.out.println("Warning: Could not retrieve properties for range class " + property.getRange() + ": " + e.getMessage());
                                canBeTransparented = false;
                            }
                        }
                    }
                }
                
                // Check if this property has a range and if it matches a subForm sectionName (only if it can be transparented)
                if (property.getRange() != null && canBeTransparented) {
                    System.out.println("Checking property: " + property.getPropLabel() + " with range: " + property.getRange());
                    
                    // Check if the form has subForms and if any subForm's sectionName matches the range
                    if (form != null && form.getSubForms() != null) {
                        for (Form subFormItem : form.getSubForms()) {
                            if (subFormItem.getSectionName() != null && subFormItem.getSectionName().equals(property.getRange())) {
                                System.out.println("Found matching subForm sectionName: " + subFormItem.getSectionName() + " for property " + property.getPropLabel());
                                try {
                                    // Get the form properties for the matching subForm
                                    subForm = getFormToModifyInternal(ontoId, subFormItem.getClassUri(), cache);
                                    System.out.println("Added subForm for property " + property.getPropLabel() + " with matching sectionName " + subFormItem.getSectionName());
                                    break; // Found the matching subForm, no need to continue searching
                                } catch (Exception e) {
                                    System.out.println("Warning: Could not retrieve subForm for property " + property.getPropLabel() + ": " + e.getMessage());
                                }
                            }
                        }
                    }
                }
                
                result.add(new PropertyDescriptorWithFormStatus(property, isShown, options, subForm));
            }
            
            // Cache the result to avoid infinite recursion and improve performance
            cache.put(classUri, result);
            
            return result;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error retrieving form and properties for modification", e);
        }
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
}
