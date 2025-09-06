package uy.com.fing.hicscan.hceanalysis.data.OntoForms.datatypes;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PropertyDescriptorWithFormStatus extends PropertyDescriptor {
    private boolean isShown;
    private List<Form.FieldOption> options;
    private List<PropertyDescriptorWithFormStatus> subForm;
    private boolean canBeTransparented;
    
    public PropertyDescriptorWithFormStatus(PropertyDescriptor property, boolean isShown) {
        super(
            property.getPropLabel(),
            property.getPropUri(),
            property.getDomainClass(),
            property.getRangeClass(),
            property.getDomain(),
            property.getRange(),
            property.getPropType(),
            property.isFunctional(),
            property.isInverseFunctional()
        );
        this.isShown = isShown;
        this.options = null;
        this.subForm = null;
        this.canBeTransparented = false;
    }
    
    public PropertyDescriptorWithFormStatus(PropertyDescriptor property, boolean isShown, List<Form.FieldOption> options) {
        super(
            property.getPropLabel(),
            property.getPropUri(),
            property.getDomainClass(),
            property.getRangeClass(),
            property.getDomain(),
            property.getRange(),
            property.getPropType(),
            property.isFunctional(),
            property.isInverseFunctional()
        );
        this.isShown = isShown;
        this.options = options;
        this.subForm = null;
        this.canBeTransparented = false;
    }
    
    public PropertyDescriptorWithFormStatus(PropertyDescriptor property, boolean isShown, List<Form.FieldOption> options, List<PropertyDescriptorWithFormStatus> subForm) {
        super(
            property.getPropLabel(),
            property.getPropUri(),
            property.getDomainClass(),
            property.getRangeClass(),
            property.getDomain(),
            property.getRange(),
            property.getPropType(),
            property.isFunctional(),
            property.isInverseFunctional()
        );
        this.isShown = isShown;
        this.options = options;
        this.subForm = subForm;
        this.canBeTransparented = false;
    }
    
    public PropertyDescriptorWithFormStatus(PropertyDescriptor property, boolean isShown, List<Form.FieldOption> options, List<PropertyDescriptorWithFormStatus> subForm, boolean canBeTransparented) {
        super(
            property.getPropLabel(),
            property.getPropUri(),
            property.getDomainClass(),
            property.getRangeClass(),
            property.getDomain(),
            property.getRange(),
            property.getPropType(),
            property.isFunctional(),
            property.isInverseFunctional()
        );
        this.isShown = isShown;
        this.options = options;
        this.subForm = subForm;
        this.canBeTransparented = canBeTransparented;
    }
} 