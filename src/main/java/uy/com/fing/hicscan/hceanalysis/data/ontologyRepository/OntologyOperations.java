package uy.com.fing.hicscan.hceanalysis.data.ontologyRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import uy.com.fing.hicscan.hceanalysis.data.ontologyRepository.enums.MedicationClassesEnum;
import uy.com.fing.hicscan.hceanalysis.data.ontologyRepository.enums.MedicationPropertiesEnum;

@Service
@AllArgsConstructor
@Slf4j
public class OntologyOperations {

    private final uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontology.OntologyRepository ontologyRepository;
    private final uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.persistence.WomanIndividualsRepository womanIndividualsRepository;

    /**
     * Creates a new individual instance of a class in memory.
     * 
     * @param ontoModel the ontology model to use
     * @param classUri the URI of the class to instantiate
     * @return the created individual, or null if the class doesn't exist
     */
    public Individual createIndividual(OntModel ontoModel, String classUri) {
        log.info("Creating individual of class {} using provided ontology model", classUri);
        
        try {
            if (ontoModel == null) {
                log.error("Ontology model is null");
                return null;
            }
            
            // Check if the class exists
            OntClass ontClass = ontoModel.getOntClass(classUri);
            if (ontClass == null) {
                log.error("Class {} does not exist in provided ontology model", classUri);
                return null;
            }
            
            // Create the individual
            Individual individual = ontClass.createIndividual();
            log.info("Successfully created individual {} of class {}", individual.getURI(), classUri);
            
            return individual;
            
        } catch (Exception e) {
            log.error("Error creating individual of class {}: {}", 
                     classUri, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Creates a new individual instance of a class with a specific URI in memory.
     * 
     * @param ontoModel the ontology model to use
     * @param classUri the URI of the class to instantiate
     * @param individualUri the specific URI for the individual
     * @return the created individual, or null if the class doesn't exist
     */
    public Individual createIndividual(OntModel ontoModel, String classUri, String individualUri) {
        log.info("Creating individual {} of class {} using provided ontology model", individualUri, classUri);
        
        try {
            if (ontoModel == null) {
                log.error("Ontology model is null");
                return null;
            }
            
            // Check if the class exists
            OntClass ontClass = ontoModel.getOntClass(classUri);
            if (ontClass == null) {
                log.error("Class {} does not exist in provided ontology model", classUri);
                return null;
            }
            
            // Create the individual with specific URI
            Individual individual = ontClass.createIndividual(individualUri);
            log.info("Successfully created individual {} of class {}", individual.getURI(), classUri);
            
            return individual;
            
        } catch (Exception e) {
            log.error("Error creating individual {} of class {}: {}", 
                     individualUri, classUri, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Adds a single property to an individual in memory.
     * 
     * @param individual the individual to add the property to
     * @param propertyUri the URI of the property to add
     * @param value the value to set for the property
     * @return true if the property was added successfully, false otherwise
     */
    public boolean addProperty(Individual individual, String propertyUri, String value) {
        if (individual == null) {
            log.error("Cannot add property to null individual");
            return false;
        }
        
        log.info("Adding property {} with value {} to individual {}", 
                 propertyUri, value, individual.getURI());
        
        try {
            OntModel ontoModel = individual.getOntModel();
            
            // Check if the property exists in the ontology
            OntProperty ontProperty = ontoModel.getOntProperty(propertyUri);
            if (ontProperty == null) {
                log.error("Property {} does not exist in the ontology", propertyUri);
                return false;
            }
            
            // Add the property to the individual
            addPropertyToIndividual(individual, propertyUri, value);
            log.info("Successfully added property {} with value {} to individual {}", 
                     propertyUri, value, individual.getURI());
            
            return true;
            
        } catch (Exception e) {
            log.error("Error adding property {} with value {} to individual {}: {}", 
                     propertyUri, value, individual.getURI(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * Adds multiple properties to an individual in memory.
     * 
     * @param individual the individual to add properties to
     * @param properties a map of property URIs to their values
     * @return a list of property URIs that were successfully added
     */
    public List<String> addProperties(Individual individual, Map<String, String> properties) {
        if (individual == null) {
            log.error("Cannot add properties to null individual");
            return new ArrayList<>();
        }
        
        log.info("Adding {} properties to individual {}", properties.size(), individual.getURI());
        
        List<String> addedProperties = new ArrayList<>();
        
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String propertyUri = entry.getKey();
            String value = entry.getValue();
            
            if (addProperty(individual, propertyUri, value)) {
                addedProperties.add(propertyUri);
            }
        }
        
        log.info("Successfully added {} out of {} properties to individual {}", 
                 addedProperties.size(), properties.size(), individual.getURI());
        return addedProperties;
    }

    /**
     * Checks if a class exists in the ontology.
     * 
     * @param ontologyId the ID of the ontology to check
     * @param classUri the URI of the class to check
     * @return true if the class exists, false otherwise
     */
    public boolean classExists(String ontologyId, String classUri) {
        try {
            OntModel ontoModel = ontologyRepository.getOntologyModelABoxByIdFor(ontologyId);
            OntClass ontClass = ontoModel.getOntClass(classUri);
            return ontClass != null;
        } catch (Exception e) {
            log.error("Error checking if class {} exists in ontology {}: {}", 
                     classUri, ontologyId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Checks if a property exists in the ontology.
     * 
     * @param ontologyId the ID of the ontology to check
     * @param propertyUri the URI of the property to check
     * @return true if the property exists, false otherwise
     */
    public boolean propertyExists(String ontologyId, String propertyUri) {
        try {
            OntModel ontoModel = ontologyRepository.getOntologyModelABoxByIdFor(ontologyId);
            OntProperty ontProperty = ontoModel.getOntProperty(propertyUri);
            return ontProperty != null;
        } catch (Exception e) {
            log.error("Error checking if property {} exists in ontology {}: {}", 
                     propertyUri, ontologyId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Gets an existing individual by URI.
     * 
     * @param ontologyId the ID of the ontology to search in
     * @param individualUri the URI of the individual to get
     * @return the individual if found, null otherwise
     */
    public Individual getIndividual(String ontologyId, String individualUri) {
        try {
            OntModel ontoModel = ontologyRepository.getOntologyModelABoxByIdFor(ontologyId);
            Individual individual = ontoModel.getIndividual(individualUri);
            return individual;
        } catch (Exception e) {
            log.error("Error getting individual {} from ontology {}: {}", 
                     individualUri, ontologyId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Adds a property to an individual, attempting to determine the correct type.
     * 
     * @param individual the individual to update
     * @param propertyUri the URI of the property to add
     * @param value the value to set (as a string)
     */
    private void addPropertyToIndividual(Individual individual, String propertyUri, String value) {
        org.apache.jena.rdf.model.Property property = ResourceFactory.createProperty(propertyUri);
        
        // Try to determine the type of the value and add accordingly
        if (value == null || value.trim().isEmpty()) {
            log.warn("Empty value provided for property {}", propertyUri);
            return;
        }

        // Check if the value looks like a URI (resource reference)
        if (isUri(value)) {
            // Add as object property (reference to another individual/resource)
            log.debug("Adding {} as object property with resource value {}", propertyUri, value);
            individual.addProperty(property, ResourceFactory.createResource(value));
        } else {
            // Try to parse as different data types
            if (isInteger(value)) {
                log.debug("Adding {} as integer literal with value {}", propertyUri, value);
                individual.addLiteral(property, Integer.parseInt(value));
            } else if (isDouble(value)) {
                log.debug("Adding {} as double literal with value {}", propertyUri, value);
                individual.addLiteral(property, Double.parseDouble(value));
            } else if (isBoolean(value)) {
                log.debug("Adding {} as boolean literal with value {}", propertyUri, value);
                individual.addLiteral(property, Boolean.parseBoolean(value));
            } else {
                // Add as string literal
                log.debug("Adding {} as string literal with value {}", propertyUri, value);
                individual.addProperty(property, value);
            }
        }
    }

    /**
     * Checks if a string looks like a URI.
     */
    private boolean isUri(String value) {
        return value.startsWith("http://") || value.startsWith("https://") || value.startsWith("urn:");
    }

    /**
     * Checks if a string can be parsed as an integer.
     */
    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if a string can be parsed as a double.
     */
    private boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if a string represents a boolean value.
     */
    private boolean isBoolean(String value) {
        return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
    }

    /**
     * Crea un medicamento y lo asocia al historial de una mujer.
     * 
     * Precondición: La mujer debe existir previamente.
     * 
     * @param ontoModel modelo de la ontología
     * @param womanId ID de la mujer existente
     * @param medicationName Nombre del medicamento
     * @param activeIngredient Ingrediente activo del medicamento
     * @param code Código del medicamento
     * @param isDiuretic Si el medicamento es diurético
     * @return true si se creó exitosamente, false en caso contrario
     */
    public boolean createMedicationForWoman(OntModel ontoModel, String womanId, String medicationName, 
                                          String activeIngredient, String code, boolean isDiuretic) {
        log.info("Creating medication {} for woman {} using provided ontology model", medicationName, womanId);
        
        try {
            if (ontoModel == null) {
                log.error("Ontology model is null");
                return false;
            }
            
            // Obtener la instancia de la mujer desde el repositorio en memoria
            Individual womanIndividual = womanIndividualsRepository.getWoman(womanId);
            if (womanIndividual == null) {
                log.error("Woman with ID {} not found in memory repository", womanId);
                return false;
            }
            log.info("Found woman individual: {} with URI: {}", womanId, womanIndividual.getURI());
            
            // 1. Instanciar el medicamento (Medication_History)
            Individual medicationInstance = createIndividual(ontoModel, MedicationClassesEnum.MEDICATION_HISTORY_CLASS.getUri());
            if (medicationInstance == null) {
                log.error("Failed to create medication instance");
                return false;
            }
            
            // Agregar propiedades básicas del medicamento
            addPropertyToIndividual(medicationInstance, MedicationPropertiesEnum.HAS_LABEL.getUri(), medicationName);
            addPropertyToIndividual(medicationInstance, MedicationPropertiesEnum.HAS_PREF_LABEL.getUri(), medicationName);
            
            // 2. Si es diurético, agregar las propiedades correspondientes
            if (isDiuretic) {
                // Crear instancia del ingrediente activo
                Individual activeIngredientInstance = createIndividual(ontoModel, MedicationClassesEnum.ACTIVE_INGREDIENT_CLASS.getUri());
                if (activeIngredientInstance == null) {
                    log.error("Failed to create active ingredient instance");
                    return false;
                }
                
                // Crear instancia de diurético
                Individual diureticInstance = createIndividual(ontoModel, MedicationClassesEnum.DIURETIC_CLASS.getUri());
                if (diureticInstance == null) {
                    log.error("Failed to create diuretic instance");
                    return false;
                }
                
                // Agregar propiedades a las instancias
                addPropertyToIndividual(activeIngredientInstance, MedicationPropertiesEnum.HAS_LABEL.getUri(), activeIngredient);
                addPropertyToIndividual(activeIngredientInstance, MedicationPropertiesEnum.HAS_PREF_LABEL.getUri(), activeIngredient);
                addPropertyToIndividual(diureticInstance, MedicationPropertiesEnum.HAS_LABEL.getUri(), "Diuretic");
                
                // DIURETICS(instActiveIngredient) - agregar el ingrediente activo como miembro del diurético
                addPropertyToIndividual(diureticInstance, "http://www.w3.org/2000/01/rdf-schema#member", activeIngredientInstance.getURI());
                
                // hasActiveIngredient(instMedicamento, instActiveIngredient)
                addPropertyToIndividual(medicationInstance, MedicationPropertiesEnum.HAS_ACTIVE_INGREDIENT.getUri(), activeIngredientInstance.getURI());
            } else {
                // Si no es diurético, solo crear el ingrediente activo
                Individual activeIngredientInstance = createIndividual(ontoModel, MedicationClassesEnum.ACTIVE_INGREDIENT_CLASS.getUri());
                if (activeIngredientInstance == null) {
                    log.error("Failed to create active ingredient instance");
                    return false;
                }
                
                addPropertyToIndividual(activeIngredientInstance, MedicationPropertiesEnum.HAS_LABEL.getUri(), activeIngredient);
                addPropertyToIndividual(activeIngredientInstance, MedicationPropertiesEnum.HAS_PREF_LABEL.getUri(), activeIngredient);
                
                // hasActiveIngredient(instMedicamento, instActiveIngredient)
                addPropertyToIndividual(medicationInstance, MedicationPropertiesEnum.HAS_ACTIVE_INGREDIENT.getUri(), activeIngredientInstance.getURI());
            }
            
            // 3. Agregar property: hasHistory(instWoman, instMedicamento)
            boolean historyAdded = addProperty(womanIndividual, MedicationPropertiesEnum.HAS_HISTORY.getUri(), medicationInstance.getURI());
            if (!historyAdded) {
                log.error("Failed to add medication to woman's history");
                return false;
            }
            
            log.info("Successfully created medication {} with code {} for woman {}", medicationName, code, womanId);
            return true;
            
        } catch (Exception e) {
            log.error("Error creating medication for woman: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Ejecuta el razonador manualmente sobre un modelo de ontología específico y devuelve los resultados.
     * Esta función permite ejecutar el razonador cuando se desee, sin que se ejecute automáticamente
     * al agregar elementos a la ontología.
     * 
     * @param ontoModel modelo de la ontología sobre la cual ejecutar el razonador
     * @return ReasoningResult con los resultados del razonador, incluyendo statements derivados y explicaciones
     */
    public ReasoningResult executeReasoner(OntModel ontoModel) {
        log.info("Executing reasoner manually on provided ontology model");
        
        try {
            if (ontoModel == null) {
                log.error("Ontology model is null");
                return new ReasoningResult(new ArrayList<>(), new ArrayList<>(), 0, false, "Ontology model is null");
            }
            
            // Verificar que el modelo tenga un razonador configurado
            if (ontoModel.getReasoner() == null) {
                log.warn("No reasoner configured on the model, but continuing with prepare()");
            } else {
                log.info("Reasoner found: {}", ontoModel.getReasoner().getClass().getSimpleName());
            }
            
            // Asegurar que el logging de derivaciones esté habilitado
            ontoModel.setDerivationLogging(true);
            
            // Forzar la ejecución del razonador
            log.info("Calling ontoModel.prepare() to execute reasoning...");
            ontoModel.prepare();
            log.info("Reasoning execution completed");
            
            // Obtener todos los statements del modelo (incluyendo los derivados)
            List<String> derivedStatements = new ArrayList<>();
            List<String> derivations = new ArrayList<>();
            
            StmtIterator stmtIterator = ontoModel.listStatements();
            int totalStatements = 0;
            int filteredStatements = 0;
            
            // URI del sujeto que queremos filtrar
            String targetSubject = "http://purl.org/ontology/breast_cancer_recommendation#NewWoman";
            
            log.info("Analyzing statements for subject: {}", targetSubject);
            
            while (stmtIterator.hasNext()) {
                Statement statement = stmtIterator.nextStatement();
                totalStatements++;
                
                // Filtrar solo las triplas donde el sujeto es NewWoman
                if (statement.getSubject() != null && 
                    statement.getSubject().getURI() != null && 
                    statement.getSubject().getURI().equals(targetSubject)) {
                    
                    filteredStatements++;
                    // Agregar el statement a la lista
                    derivedStatements.add(statement.toString());
                    log.debug("Found statement for NewWoman: {}", statement.toString());
                    
                    // Si es una relación hasAge, también capturar las propiedades del individuo de edad
                    if (statement.getPredicate() != null && 
                        statement.getPredicate().getURI() != null && 
                        statement.getPredicate().getURI().equals("http://purl.org/ontology/breast_cancer_recommendation#hasAge")) {
                        
                        // Obtener el individuo de edad relacionado
                        if (statement.getObject() != null && statement.getObject().isResource()) {
                            String objectUri = statement.getObject().asResource().getURI();
                            if (objectUri != null) {
                                Individual ageIndividual = ontoModel.getIndividual(objectUri);
                                if (ageIndividual != null) {
                                    // Capturar las propiedades del individuo de edad
                                    ageIndividual.listProperties().forEachRemaining(prop -> {
                                        String ageProp = String.format("[AGE_INDIVIDUAL] %s -> %s", 
                                            prop.getPredicate().getURI(), 
                                            prop.getObject().toString());
                                        derivedStatements.add(ageProp);
                                        log.debug("Found age individual property: {}", ageProp);
                                    });
                                } else {
                                    log.debug("Age individual not found for URI: {}", objectUri);
                                }
                            } else {
                                log.debug("Object URI is null for hasAge statement: {}", statement.toString());
                            }
                        } else {
                            log.debug("Object is not a resource for hasAge statement: {}", statement.toString());
                        }
                    }
                    
                    // Intentar obtener la derivación para este statement
                    try {
                        ontoModel.getDerivation(statement).forEachRemaining(derivation -> {
                            derivations.add(derivation.toString());
                            log.debug("Found derivation: {}", derivation.toString());
                        });
                    } catch (Exception e) {
                        // Algunos statements pueden no tener derivaciones
                        log.debug("No derivation available for statement: {}", statement.toString());
                    }
                }
            }
            
            log.info("Reasoner execution completed. Total statements: {}, Filtered statements: {}, Derivations found: {}", 
                    totalStatements, filteredStatements, derivations.size());
            
            // Log de todos los statements encontrados para debugging
            log.info("=== REASONING RESULTS DEBUG ===");
            log.info("Total statements found: {}", totalStatements);
            log.info("Filtered statements for NewWoman: {}", filteredStatements);
            log.info("Derivations found: {}", derivations.size());
            
            // Mostrar algunos ejemplos de statements encontrados
            log.info("Sample statements found:");
            derivedStatements.stream().limit(10).forEach(stmt -> log.info("  - {}", stmt));
            
            if (log.isDebugEnabled()) {
                log.debug("All derived statements for NewWoman:");
                derivedStatements.forEach(stmt -> log.debug("  - {}", stmt));
                log.debug("All derivations found:");
                derivations.forEach(deriv -> log.debug("  - {}", deriv));
            }
            
            return new ReasoningResult(derivedStatements, derivations, filteredStatements, true, null);
            
        } catch (Exception e) {
            log.error("Error executing reasoner on provided model: {}", e.getMessage(), e);
            return new ReasoningResult(new ArrayList<>(), new ArrayList<>(), 0, false, e.getMessage());
        }
    }

}
