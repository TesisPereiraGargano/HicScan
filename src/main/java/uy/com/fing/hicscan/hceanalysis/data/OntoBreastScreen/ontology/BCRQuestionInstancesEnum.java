package uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontology;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Este enumerado tiene todas las clases de la ontología breast_cancer_recommendation junto
 * a sus uris.
 */
@AllArgsConstructor
@Getter
public enum BCRQuestionInstancesEnum {

    IBIS_PERSONAL_AGE_QUESTION("http://purl.org/ontology/breast_cancer_recommendation#IBIS_age_question"),
    IBIS_PERSONAL_WEIGHT_QUESTION("http://purl.org/ontology/breast_cancer_recommendation#IBIS_weight_question"),
    IBIS_PERSONAL_HEIGHT_QUESTION("http://purl.org/ontology/breast_cancer_recommendation#IBIS_height_question"),
    IBIS_PERSONAL_BRAST_DENSITY("http://purl.org/ontology/breast_cancer_recommendation#IBIS_breast_density_question"),

    IBIS_HORMONAL_MENARCHE_AGE("http://purl.org/ontology/breast_cancer_recommendation#IBIS_menarche_age_question"),
    IBIS_HORMONAL_CHILDS("http://purl.org/ontology/breast_cancer_recommendation#IBIS_has_childs_question"),
    IBIS_HORMONAL_CHILDS_AGE("http://purl.org/ontology/breast_cancer_recommendation#IBIS_first_child_birth_age_question"),
    IBIS_HORMONAL_MENOPAUSE("http://purl.org/ontology/breast_cancer_recommendation#IBIS_has_menopause_question"),
    IBIS_HORMONAL_MENOPAUSE_AGE("http://purl.org/ontology/breast_cancer_recommendation#IBIS_menopause_age_question"),
    IBIS_HORMONAL_HRT("http://purl.org/ontology/breast_cancer_recommendation#IBIS_hormonal_therapy_question"),
    IBIS_HORMONAL_ESTROGEN("http://purl.org/ontology/breast_cancer_recommendation#IBIS_hormonal_therapy_treatment_question"),
    IBIS_HORMONAL_HRT_LENGHT("http://purl.org/ontology/breast_cancer_recommendation#IBIS_HRT_lenght_years_use_question"),
    IBIS_HORMONAL_HRT_YEARS_AGO("http://purl.org/ontology/breast_cancer_recommendation#IBIS_HRT_years_ago_question"),
    IBIS_HORMONAL_HRT_HOW_LONG("http://purl.org/ontology/breast_cancer_recommendation#IBIS_HRT_how_long_use_question"),
    IBIS_GEN_BRCA("http://purl.org/ontology/breast_cancer_recommendation#IBIS_BRCA_gen_question"),
    IBIS_OVARIAN_CANCER("http://purl.org/ontology/breast_cancer_recommendation#IBIS_ovarian_cancer_question"),
    IBIS_OVARIAN_CANCER_AGE("http://purl.org/ontology/breast_cancer_recommendation#IBIS_ovarian_cancer_age_question"),
    IBIS_BREAST_DISEASE("http://purl.org/ontology/breast_cancer_recommendation#IBIS_breast_biopsy_question"),
    IBIS_FAMILY_ASHKENAZI("http://purl.org/ontology/breast_cancer_recommendation#IBIS_ashkenazi_ancestry_question"),

    IBIS_MOTHER_AGE("http://purl.org/ontology/breast_cancer_recommendation#IBIS_mother_age_question"),
    IBIS_MOTHER_BC("http://purl.org/ontology/breast_cancer_recommendation#IBIS_breast_cancer_mother_question"),
    IBIS_MOTHER_BC_START("http://purl.org/ontology/breast_cancer_recommendation#IBIS_mother_age_bc_start_question"),
    IBIS_MOTHER_BC_BILATERAL("http://purl.org/ontology/breast_cancer_recommendation#IBIS_bilateral_bc_mother_question"),
    IBIS_MOTHER_BC_BILATERAL_AGE("http://purl.org/ontology/breast_cancer_recommendation#IBIS_mother_bilateral_bc_age_question"),
    IBIS_MOTHER_OVARIAN("http://purl.org/ontology/breast_cancer_recommendation#IBIS_ovarian_cancer_mother_question"),
    IBIS_MOTHER_OVARIAN_AGE("http://purl.org/ontology/breast_cancer_recommendation#IBIS_mother_age_oc_start_question"),
    IBIS_MOTHER_BRCA("http://purl.org/ontology/breast_cancer_recommendation#IBIS_BRCA_gen_mother_question"),

    IBIS_SISTER_AGE("http://purl.org/ontology/breast_cancer_recommendation#IBIS_sister_age_question"),
    IBIS_SISTER_BC("http://purl.org/ontology/breast_cancer_recommendation#IBIS_sister_breast_cancer_question"),
    IBIS_SISTER_BC_START("http://purl.org/ontology/breast_cancer_recommendation#IBIS_sister_age_bc_start_question"),
    IBIS_SISTER_BC_BILATERAL("http://purl.org/ontology/breast_cancer_recommendation#IBIS_sister_bilateral_bc_question"),
    IBIS_SISTER_BC_BILATERAL_AGE("http://purl.org/ontology/breast_cancer_recommendation#IBIS_sister_bilateral_bc_age_question"),
    IBIS_SISTER_OVARIAN("http://purl.org/ontology/breast_cancer_recommendation#IBIS_sister_ovarian_cancer_question"),
    IBIS_SISTER_OVARIAN_AGE("http://purl.org/ontology/breast_cancer_recommendation#IBIS_sister_age_oc_start_question"),
    IBIS_SISTER_BRCA("http://purl.org/ontology/breast_cancer_recommendation#IBIS_sister_BRCA_gen_question"),

    ACS_CHEST_RADIOTERAPY_YOUNG_AGE_QUESTION("http://purl.org/ontology/breast_cancer_recommendation#ACS_chest_radiotherapy_young_age"),
    ACS_HISTORY_BREAST_CANCER_QUESTION("http://purl.org/ontology/breast_cancer_recommendation#ACS_history_breast_cancer"),
    ACS_GENETIC_MUTATION_QUESTION("http://purl.org/ontology/breast_cancer_recommendation#ACS_genetic_mutation_question"),
    ACS_AGE_QUESTION("http://purl.org/ontology/breast_cancer_recommendation#ACS_age_question"),

    UY_AGE_QUESTION("http://purl.org/ontology/breast_cancer_recommendation#UY_age_question"),
    UY_HEREDITARY_RISK_QUESTION("http://purl.org/ontology/breast_cancer_recommendation#UY_hereditary_risk_question"),
    UY_CHEST_RADIOTERAPHY_QUESTION("http://purl.org/ontology/breast_cancer_recommendation#UY_chest_radiotherapy_question"),
    UY_HIPERPLASIA_ATIPIA_QUESTION("http://purl.org/ontology/breast_cancer_recommendation#UY_hiperplasia_atipia_question");

    private final String uri;
}

