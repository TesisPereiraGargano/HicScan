package uy.com.fing.hicscan.hceanalysis.usecases;

import org.springframework.stereotype.Service;
import uy.com.fing.hicscan.hceanalysis.data.OntoForms.OntoForms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
}
