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
import uy.com.fing.hicscan.hceanalysis.dto.DatosHCE;
import uy.com.fing.hicscan.hceanalysis.dto.SustanciaAdministrada;
import uy.com.fing.hicscan.hceanalysis.dto.Droga;

import static uy.com.fing.hicscan.hceanalysis.utils.FunctionUtils.toCamelCase;

@Service
@AllArgsConstructor
@Slf4j
public class OntologyOperations {

    private final uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontology.OntologyRepository ontologyRepository;
    private final uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.persistence.WomanIndividualsRepository womanIndividualsRepository;


    /**
     * Crea una nueva clase en la ontología.
     * 
     * @param ontoModel el modelo de la ontología donde crear la clase
     * @param className el nombre de la nueva clase
     * @param parentClassUri la URI de la clase padre (superclase)
     * @param classUri la URI específica para la nueva clase
     * @return la clase creada, o null si hubo un error
     */
    public OntClass createNewClass(OntModel ontoModel, String className, String parentClassUri, String classUri) {
        log.info("Creating new class {} with parent {} using provided ontology model", className, parentClassUri);
        
        try {
            if (ontoModel == null) {
                log.error("Ontology model is null");
                return null;
            }
            
            // Verificar que la clase padre existe
            OntClass parentClass = ontoModel.getOntClass(parentClassUri);
            if (parentClass == null) {
                log.error("Parent class {} does not exist in provided ontology model", parentClassUri);
                return null;
            }
            
            // Crear la nueva clase
            OntClass newClass = ontoModel.createClass(classUri);
            if (newClass == null) {
                log.error("Failed to create class with URI {}", classUri);
                return null;
            }
            
            // Establecer la clase padre (hacer que sea subclase)
            newClass.addSuperClass(parentClass);
            
            // Agregar un label a la clase
            newClass.addLabel(className, "en");
            
            log.info("Successfully created class {} as subclass of {}", className, parentClassUri);
            return newClass;
            
        } catch (Exception e) {
            log.error("Error creating class {} with parent {}: {}", 
                    className, parentClassUri, e.getMessage(), e);
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
     * Crea medicamentos desde datos HCE procesados y los asocia al historial de una mujer.
     *
     * Precondición: La mujer debe existir previamente.
     *
     * @param ontoModel modelo de la ontología
     * @param womanId ID de la mujer existente
     * @param datosHCE Datos procesados de la HCE con medicamentos clasificados
     * @return true si se crearon exitosamente, false en caso contrario
     */
    public boolean createMedicationsFromHCE(OntModel ontoModel, String womanId, DatosHCE datosHCE) {
        log.info("Creo medicamentos de la HCE para la mujer {}", womanId);

        try {
            if (datosHCE == null || datosHCE.getMedicamentos() == null) {
                log.error("DatosHCE o mecicamentos en null");
                return false;
            }

            DatosHCE.Medicamentos medicamentos = datosHCE.getMedicamentos();
            boolean allSuccess = true;

            // Proceso diuréticos
            if (medicamentos.getClasificados() != null &&
                    medicamentos.getClasificados().getDiureticos() != null) {
                for (SustanciaAdministrada sustancia : medicamentos.getClasificados().getDiureticos()) {
                    boolean success = createMedicationForWoman(ontoModel, womanId, sustancia, true);
                    allSuccess = allSuccess && success;
                }
            }

            // Procesar no diuréticos
            if (medicamentos.getClasificados() != null &&
                    medicamentos.getClasificados().getNoDiureticos() != null) {
                for (SustanciaAdministrada sustancia : medicamentos.getClasificados().getNoDiureticos()) {
                    boolean success = createMedicationForWoman(ontoModel, womanId, sustancia, false);
                    allSuccess = allSuccess && success;
                }
            }

            // Procesar no clasificados (asumiendo que no son diuréticos)
            if (medicamentos.getNoClasificados() != null) {
                for (SustanciaAdministrada sustancia : medicamentos.getNoClasificados()) {
                    boolean success = createMedicationForWoman(ontoModel, womanId, sustancia, false);
                    allSuccess = allSuccess && success;
                }
            }

            return allSuccess;

        } catch (Exception e) {
            log.error("Error creating medications from HCE for woman {}: {}", womanId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Crea un medicamento individual y lo asocia al historial de una mujer.
     *
     * @param ontoModel modelo de la ontología
     * @param womanId ID de la mujer existente
     * @param sustanciaAdministrada Sustancia administrada con sus drogas asociadas
     * @param isDiuretic Indica si el medicamento ya fue clasificado como diurético
     * @return true si se creó exitosamente, false en caso contrario
     */
    private boolean createMedicationForWoman(OntModel ontoModel, String womanId,
                                             SustanciaAdministrada sustanciaAdministrada,
                                             boolean isDiuretic) {

        log.info("Creating medication {} for woman {} (diuretic: {})",
                sustanciaAdministrada.getName(), womanId, isDiuretic);

        try {
            if (ontoModel == null) {
                log.error("Ontology model is null");
                return false;
            }

            Individual womanIndividual = womanIndividualsRepository.getWoman(womanId);
            if (womanIndividual == null) {
                log.error("Woman with ID {} not found in memory repository", womanId);
                return false;
            }
            log.info("Found woman individual: {} with URI: {}", womanId, womanIndividual.getURI());

            // Creo la clase del medicamento
            // Formateo de nombre para generar una URI sin caracteres especiales
            String medicationClassUri = "http://purl.org/ontology/breast_cancer_recommendation#" + toCamelCase(sustanciaAdministrada.getName());
            createNewClass(ontoModel, sustanciaAdministrada.getName(),
                    MedicationClassesEnum.MEDICATION_HISTORY_CLASS.getUri(), medicationClassUri);

            // Creo la instancia del medicamento
            String medicationInstanceUri = "http://purl.org/ontology/breast_cancer_recommendation#" +
                    toCamelCase(sustanciaAdministrada.getName())+ "NewWoman";
            Individual medicationInstance = createIndividual(ontoModel, medicationClassUri, medicationInstanceUri);

            // Agrego el medicamento al historial de la mujer
            String medicationHistoryHasHistoryUri = MedicationPropertiesEnum.HAS_HISTORY.getUri();
            addProperty(womanIndividual, medicationHistoryHasHistoryUri, medicationInstanceUri);

            // Si es diurético, proceso sus drogas para agregar ingredientes activos
            if (isDiuretic && sustanciaAdministrada.getDrugs() != null) {
                for (Droga droga : sustanciaAdministrada.getDrugs()) {
                    // Creo una instancia del ingrediente activo en la clase DIURETICS
                    String activeIngredientInstanceUri = "http://purl.org/ontology/breast_cancer_recommendation#" + toCamelCase(droga.getNombre());
                    String diureticClassUri = MedicationClassesEnum.DIURETIC_CLASS.getUri();
                    Individual diureticInstance = createIndividual(ontoModel, diureticClassUri, activeIngredientInstanceUri);

                    if (diureticInstance == null) {
                        log.error("Failed to create diuretic instance for drug: {}", droga.getNombre());
                        return false;
                    }

                    // Le agrego el ingrediente activo al medicamento
                    String medicationPropertyHasActiveIngredientUri = MedicationPropertiesEnum.HAS_ACTIVE_INGREDIENT.getUri();
                    addProperty(medicationInstance, medicationPropertyHasActiveIngredientUri, activeIngredientInstanceUri);

                    log.info("Added diuretic ingredient {} to medication {}", droga.getNombre(), sustanciaAdministrada.getName());
                }
            }

        } catch (Exception e) {
            log.error("Error creating medication {} for woman {}: {}",
                    sustanciaAdministrada.getName(), womanId, e.getMessage(), e);
            return false;
        }

        return true;
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
            
            // Obtener todos los statements del modelo (incluyendo los derivados)
            List<String> derivedStatements = new ArrayList<>();
            List<String> derivations = new ArrayList<>();
            
            StmtIterator stmtIterator = ontoModel.listStatements();
            int totalStatements = 0;
            int filteredStatements = 0;
            
            // URI del sujeto que queremos filtrar
             String targetSubject = "http://purl.org/ontology/breast_cancer_recommendation#NewWoman";

            // URI del predicado que queremos excluir
            String excludedPredicate = "http://www.w3.org/2002/07/owl#differentFrom";
            
            log.info("Analyzing statements for subject: {}", targetSubject);
            log.info("Excluding statements with predicate: {}", excludedPredicate);
            
            while (stmtIterator.hasNext()) {
                Statement statement = stmtIterator.nextStatement();
                totalStatements++;
                
                // Filtrar solo las triplas donde el sujeto es NewWoman
                if (statement.getSubject() != null && 
                    statement.getSubject().getURI() != null && 
                    statement.getSubject().getURI().equals(targetSubject)) {
                    
                    // Excluir triplas cuyo predicado sea owl:differentFrom
                    if (statement.getPredicate() != null && 
                        statement.getPredicate().getURI() != null && 
                        statement.getPredicate().getURI().equals(excludedPredicate)) {
                        log.debug("Excluding statement with owl:differentFrom predicate: {}", statement.toString());
                        continue;
                    }
                    
                    filteredStatements++;
                    // Agregar el statement a la lista
                    derivedStatements.add(statement.toString());
                    log.debug("Found statement for NewWoman: {}", statement.toString());
                    
                    
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
