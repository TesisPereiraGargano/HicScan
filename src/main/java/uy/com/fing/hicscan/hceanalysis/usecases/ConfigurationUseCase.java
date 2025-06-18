package uy.com.fing.hicscan.hceanalysis.usecases;

import org.springframework.stereotype.Service;
import uy.com.fing.hicscan.hceanalysis.data.OntoForms.OntoForms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class ConfigurationUseCase {
    
    private final OntoForms ontoForms;
    
    public ConfigurationUseCase() {
        this.ontoForms = new OntoForms();
    }
    
    public String createOntology(String fileName, byte[] fileContent) {
        try {
            // Create a temporary file to pass to OntoForms
            File tempFile = createTempFile(fileName, fileContent);
            
            // Upload the ontology using OntoForms and get the returned URL
            String ontologyUrl = ontoForms.uploadOntology(tempFile);
            
            // Clean up the temporary file
            tempFile.delete();
            
            // Return the ontology URL from the upload
            return ontologyUrl;
            
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error processing ontology file", e);
        }
    }
    
    private File createTempFile(String fileName, byte[] fileContent) throws IOException {
        // Create a temporary file with the original filename extension
        String extension = getFileExtension(fileName);
        File tempFile = File.createTempFile("ontology_", extension);
        
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
