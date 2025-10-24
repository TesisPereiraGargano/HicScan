package uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class WomanHistoryData {

    //Personal Risk Factor
    private Integer age; //IBIS age question
    private Double weight; //IBIS weight question
    private Double height; //IBIS height question
    private String breastDensity; // IBIS breast density question

    //Hormonal
    private Integer ageFirstMenarche; //IBIS menarche age question
    private Boolean hasGivenBirthOrOneOrMoreChildren; // IBIS has childs question
    private Integer ageFirstLivingChild; //IBIS first child birth age question
    private String menopauseStatus; //IBIS has menopause question
    private Integer menopauseAge; //TODO falta esta pregunta en ontología.
    private String trhStatus; //IBIS hormonal therapy question
    private String trhType; //TODO falta esta pregunta en ontología.
    private String trhLength; //TODO falta esta pregunta en ontología.
    private String trhLastUse; //TODO falta esta pregunta en ontología.
    private String trhIntendedUseLength; //TODO falta esta pregunta en ontología

    //Genetic
    private String brca; //IBIS BRCA gen question

    //Breast Disease
    private String breastBiopsy; //IBIS breast biopsy question
    //TODO falta multiselección.

    //Ovarian disease
    private Boolean ovarianCancer; //IBIS ovarian cancer question.
    private Integer ageDiagnosedOvarianCancer; //TODO falta esta pregunta en ontología.

    //Family
    private Boolean ashkenaziInheritance; //IBIS ahkenazi ancestry question
    private Integer motherCurrentAgeOrAgeAtDeath; //IBIS mother age question
    private Boolean motherBreastCancer; //IBIS breast cancer mother question
    private Boolean motherBreastCancerBilateral; //IBIS bilateral bc mother question
    private Integer motherAgeAtStartOfBC; // IBIS mother age bc start question
    private Integer motherAgeAtDiagnosisOfSecondBC; //TODO falta esta pregunta en ontología.
    private Boolean motherOvarianCancer; //IBIS ovarial cancer mother question
    private Integer motherAgeAtStartOfOvarianCancer; //TODO falta esta pregunta en ontología.
    private String motherBRCAGen; //IBIS BRCA gen mother question

    private Integer sisterCurrentAgeOrAgeAtDeath;
    private Boolean sisterBreastCancer;
    private Integer sisterAgeAthStartOfBC;
    private Boolean sisterBreastCancerBilateral;
    private Integer sisterAgeAtDiagnosisOfSecondBC;
    private Boolean sisterOvarianCancer;
    private Integer sisterAgeAtStartOfOvarianCancer;
    private String  sisterBRCAGen;
}

