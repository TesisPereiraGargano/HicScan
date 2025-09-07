package uy.com.fing.hicscan.hceanalysis.data.OntoForms.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyDescriptor {
    private String propLabel;
    private String propUri;

    @JsonIgnore
    private Object domainClass; // Placeholder for OntResource, not used in JSON
    @JsonIgnore
    private Object rangeClass;  // Placeholder for OntResource, not used in JSON

    private String domain;
    private String range;

    private String propType;
    private boolean functional;
    private boolean inverseFunctional;
} 