package uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontoforms;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Form {

    private String classUri;
    private String sectionName;
    private List<FormField> fields;
    private List<Form> subForms;
    private int sectionOrder;


    public void addSubSection(Form subSection) {
        subForms.add(subSection);
        subForms.sort(Comparator.comparingInt(Form::getSectionOrder));
    }

    public void addField(FormField field) {
        fields.add(field);
        fields.sort(Comparator.comparingInt(FormField::getOrder));
    }

    public void removeAllFields() {
        this.fields.clear();
    }

    public List<Form> removeSubForms() {
        List<Form> subForms1 = this.getSubForms();

        ArrayList<Form> objects = new ArrayList<>(subForms1);

        this.subForms.clear();
        return objects;
    }

    public record FieldOption(String label, String uri) {}

    @Data
    @NoArgsConstructor
    @SuperBuilder
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "classType")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = DatatypeField.class, name = "datatype-field"),
            @JsonSubTypes.Type(value = ObjectField.class, name = "object-field"),
    })
    public static abstract class FormField {
        private String label;
        private String uri;
        private int order;
        private Boolean editable;

        public abstract String getType();
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @SuperBuilder
    @JsonTypeName("datatype-field")
    public static class DatatypeField extends FormField {

        private String datatype;
        private String value;

        @Override
        public String getType() {
            return datatype;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @SuperBuilder
    @JsonTypeName("object-field")
    public static class ObjectField extends FormField {

        private List<FieldOption> options;
        private boolean singleOption;
        private FieldOption selectedOption;

        @Override
        public String getType() {
            return singleOption ? "single-option" : "multi-option";
        }
    }
}

