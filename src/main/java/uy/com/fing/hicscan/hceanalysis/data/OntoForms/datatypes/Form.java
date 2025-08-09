package uy.com.fing.hicscan.hceanalysis.data.OntoForms.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Form {

    private String classUri;
    private String sectionName;
    private List<FormField> fields;
    private List<Form> subForms;

    public Form(String classUri, String sectionName) {
        this.classUri = classUri;
        this.sectionName = sectionName;
        this.fields = new ArrayList<>();
        this.subForms = new ArrayList<>();
    }

    public Form(String classUri, String sectionName, List<FormField> fields) {
        this.classUri = classUri;
        this.sectionName = sectionName;
        this.fields = fields;
        this.subForms = new ArrayList<>();
    }

    public void addSubSection(Form subSection) {
        subForms.add(subSection);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FieldOption {
        private String label;
        private String uri;
        
        public FieldOption() {}
        
        public FieldOption(String label, String uri) {
            this.label = label;
            this.uri = uri;
        }
        
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public String getUri() { return uri; }
        public void setUri(String uri) { this.uri = uri; }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "classType")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = DatatypeField.class, name = "datatype-field"),
            @JsonSubTypes.Type(value = ObjectField.class, name = "object-field"),
    })
    public static abstract class FormField {
        private String label;
        private String uri;
        private Integer order;

        public abstract String getType();
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @JsonTypeName("datatype-field")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DatatypeField extends FormField {

        private String datatype;

        @Override
        public String getType() {
            return datatype;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @JsonTypeName("object-field")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ObjectField extends FormField {

        private List<FieldOption> options;
        private boolean singleOption;
        private String type;

        @Override
        public String getType() {
            return type != null ? type : (singleOption ? "single-option" : "multi-option");
        }
    }
} 