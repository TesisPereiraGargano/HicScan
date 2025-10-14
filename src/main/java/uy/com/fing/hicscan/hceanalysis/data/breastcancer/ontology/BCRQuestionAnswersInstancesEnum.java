package uy.com.fing.hicscan.hceanalysis.data.breastcancer.ontology;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Este enumerado tiene todas las clases de la ontología breast_cancer_recommendation junto
 * a sus uris.
 */
@AllArgsConstructor
@Getter
public enum BCRQuestionAnswersInstancesEnum {
    IBIS_PERSONAL_BREAST_DENSITY_HETEROG_DENSE("http://purl.org/ontology/breast_cancer_recommendation#IBIS_breast_density_heterog_dense_value"),
    IBIS_PERSONAL_BREAST_DENSITY_FATTY("http://purl.org/ontology/breast_cancer_recommendation#IBIS_breast_density_fatty_value"),
    IBIS_PERSONAL_BREAST_DENSITY_EXTREME("http://purl.org/ontology/breast_cancer_recommendation#IBIS_breast_density_extrem_dense_value"),
    IBIS_PERSONAL_BREAST_DENSITY_AVG("http://purl.org/ontology/breast_cancer_recommendation#IBIS_breast_density_average_value"),
    IBIS_HORMONAL_HAS_CHILDS_UNKNOWN("http://purl.org/ontology/breast_cancer_recommendation#IBIS_has_childs_unknown_value"),
    IBIS_HORMONAL_HAS_CHILDS_YES("http://purl.org/ontology/breast_cancer_recommendation#IBIS_has_childs_yes_value"),
    IBIS_HORMONAL_HAS_CHILDS_NO("http://purl.org/ontology/breast_cancer_recommendation#IBIS_has_childs_no_value"),
    IBIS_HORMONAL_HAS_MENOPAUSE_NO("http://purl.org/ontology/breast_cancer_recommendation#IBIS_has_menopause_no_value"),
    IBIS_HORMONAL_HAS_MENOPAUSE_YES("http://purl.org/ontology/breast_cancer_recommendation#IBIS_has_menopause_yes_value"),
    IBIS_HORMONAL_HAS_MENOPAUSE_NOW("http://purl.org/ontology/breast_cancer_recommendation#IBIS_has_menopause_now_value"),
    IBIS_HORMONAL_HAS_MENOPAUSE_UNKNOWN("http://purl.org/ontology/breast_cancer_recommendation#IBIS_has_menopause_unknown_value"),
    IBIS_HORMONAL_HRT_NOW("http://purl.org/ontology/breast_cancer_recommendation#IBIS_horm_therapy_now_value"),
    IBIS_HORMONAL_HRT_NEVER("http://purl.org/ontology/breast_cancer_recommendation#IBIS_horm_therapy_never_value"),
    IBIS_HORMONAL_HRT_5_YEARS_MORE("http://purl.org/ontology/breast_cancer_recommendation#IBIS_horm_therapy_5_years_more_value"),
    IBIS_HORMONAL_HRT_5_YEARS_LESS("http://purl.org/ontology/breast_cancer_recommendation#IBIS_horm_therapy_5_years_less_value"),
    IBIS_HORMONAL_HRT_ESTROGEN("http://purl.org/ontology/breast_cancer_recommendation#IBIS_hormonal_treatment_estrogen_value"),
    IBIS_HORMONAL_HRT_COMBINED("http://purl.org/ontology/breast_cancer_recommendation#IBIS_hormonal_treatment_combined_value"),
    IBIS_BRCA_GEN_BRCA1("http://purl.org/ontology/breast_cancer_recommendation#IBIS_BRCA_gen_BRCA1_value"),
    IBIS_BRCA_GEN_BRCA2("http://purl.org/ontology/breast_cancer_recommendation#IBIS_BRCA_gen_BRCA2_value"),
    IBIS_BRCA_GEN_TESTED_NORMAL("http://purl.org/ontology/breast_cancer_recommendation#IBIS_BRCA_gen_test_normal_value"),
    IBIS_BRCA_UNKNOWN("http://purl.org/ontology/breast_cancer_recommendation#IBIS_BRCA_gen_unknown_value"),
    IBIS_OC_YES("http://purl.org/ontology/breast_cancer_recommendation#IBIS_ovarian_cancer_yes_value"),
    IBIS_OC_NO("http://purl.org/ontology/breast_cancer_recommendation#IBIS_ovarian_cancer_no_value"),
    IBIS_BC_BIOPSY_UNKNOWN("http://purl.org/ontology/breast_cancer_recommendation#IBIS_breast_biopsy_unknown_value"),
    IBIS_BC_BIOPSY_NO_VALUE("http://purl.org/ontology/breast_cancer_recommendation#IBIS_breast_biopsy_without_value"),
    IBIS_BC_BIOPSY_HIPERPLACIA_ATIPIA("http://purl.org/ontology/breast_cancer_recommendation#IBIS_breast_biopsy_hiperplasia_atipica_value"),
    IBIS_BC_BIOPSY_HIPERPLACIA_NO_ATIPIA("http://purl.org/ontology/breast_cancer_recommendation#IBIS_breast_biopsy_hiperplasia_no_atipia_value"),
    IBIS_BC_BIOPSY_CARCINOMA("http://purl.org/ontology/breast_cancer_recommendation#IBIS_breast_biopsy_carcinoma_lob_is_value"),
    IBIS_FAMILY_ASHKENAZI_YES("http://purl.org/ontology/breast_cancer_recommendation#IBIS_ahkenazi_ancestry_yes_value"),
    IBIS_FAMILY_ASHKENAZY_NO("http://purl.org/ontology/breast_cancer_recommendation#IBIS_ahkenazi_ancestry_no_value"),
    IBIS_FAMILY_MOTHER_BC_YES("http://purl.org/ontology/breast_cancer_recommendation#IBIS_breast_cancer_mother_yes_value"),
    IBIS_FAMILY_MOTHER_BC_NO("http://purl.org/ontology/breast_cancer_recommendation#IBIS_breast_cancer_mother_no_value"),
    IBIS_FAMILY_MOTHER_BC_BILATERAL_YES("http://purl.org/ontology/breast_cancer_recommendation#IBIS_bilateral_bc_mother_yes_value"),
    IBIS_FAMILY_MOTHER_BC_BILATERAL_NO("http://purl.org/ontology/breast_cancer_recommendation#IBIS_bilateral_bc_mother_no_value"),
    IBIS_FAMILY_MOTHER_OC_YES("http://purl.org/ontology/breast_cancer_recommendation#IBIS_ovarial_cancer_mother_yes_value"),
    IBIS_FAMILY_MOTHER_OC_NO("http://purl.org/ontology/breast_cancer_recommendation#IBIS_ovarial_cancer_mother_no_value"),
    IBIS_FAMILY_MOTHER_BRCA1("http://purl.org/ontology/breast_cancer_recommendation#IBIS_BRCA_gen_mother_BRCA1_value"),
    IBIS_FAMILY_MOTHER_BRCA2("http://purl.org/ontology/breast_cancer_recommendation#IBIS_BRCA_gen_mother_BRCA2_value"),
    IBIS_FAMILY_MOTHER_BRCA_TESTED_NORMAL("http://purl.org/ontology/breast_cancer_recommendation#IBIS_BRCA_gen_mother_test_normal_value"),
    IBIS_FAMILY_MOTHER_BRCA_UNKNOWN("http://purl.org/ontology/breast_cancer_recommendation#IBIS_BRCA_gen_mother_unknown_value"),
    IBIS_FAMILY_SISTER_BC_YES("http://purl.org/ontology/breast_cancer_recommendation#IBIS_sister_breast_cancer_yes_value"),
    IBIS_FAMILY_SISTER_BC_NO("http://purl.org/ontology/breast_cancer_recommendation#IBIS_sister_breast_cancer_no_value"),
    IBIS_FAMILY_SISTER_BC_BILATERAL_YES("http://purl.org/ontology/breast_cancer_recommendation#IBIS_sister_bilateral_bc_yes_value"),
    IBIS_FAMILY_SISTER_BC_BILATERAL_NO("http://purl.org/ontology/breast_cancer_recommendation#IBIS_sister_bilateral_bc_no_value"),
    IBIS_FAMILY_SISTER_OC_YES("http://purl.org/ontology/breast_cancer_recommendation#IBIS_sister_ovarial_cancer_yes_value"),
    IBIS_FAMILY_SISTER_OC_NO("http://purl.org/ontology/breast_cancer_recommendation#IBIS_sister_ovarial_cancer_no_value"),
    IBIS_FAMILY_SISTER_BRCA1("http://purl.org/ontology/breast_cancer_recommendation#IBIS_sister_BRCA_gen_BRCA1_value"),
    IBIS_FAMILY_SISTER_BRCA2("http://purl.org/ontology/breast_cancer_recommendation#IBIS_sister_BRCA_gen_BRCA2_value"),
    IBIS_FAMILY_SISTER_BRCA_TESTED_NORMAL("http://purl.org/ontology/breast_cancer_recommendation#IBIS_sister_BRCA_gen_test_normal_value"),
    IBIS_FAMILY_SISTER_BRCA_UNKNOWN("http://purl.org/ontology/breast_cancer_recommendation#IBIS_sister_BRCA_gen_unknown_value"),

    ACS_CHEST_RADIOTERAPY_YOUNG_AGE_YES("http://purl.org/ontology/breast_cancer_recommendation#ACS_chest_radiotherapy_young_age_yes_value"),
    ACS_CHEST_RADIOTERAPY_YOUNG_AGE_NO("http://purl.org/ontology/breast_cancer_recommendation#ACS_chest_radiotherapy_young_age_no_value"),
    ACS_HISTORY_BREAST_CANCER_YES("http://purl.org/ontology/breast_cancer_recommendation#ACS_history_breast_cancer_yes_value"),
    ACS_HISTORY_BREAST_CANCER_NO("http://purl.org/ontology/breast_cancer_recommendation#ACS_history_breast_cancer_no_value"),
    ACS_GENETIC_MUTATION_YES("http://purl.org/ontology/breast_cancer_recommendation#ACS_genetic_mutation_yes_value"),
    ACS_GENETIC_MUTATION_NO("http://purl.org/ontology/breast_cancer_recommendation#ACS_genetic_mutation_no_value"),


    UY_HEREDITARY_RISK_YES("http://purl.org/ontology/breast_cancer_recommendation#UY_hereditary_risk_yes_value"),
    UY_HEREDITARY_RISK_NO("http://purl.org/ontology/breast_cancer_recommendation#UY_hereditary_risk_no_value"),
    UY_CHEST_RADIOTERAPHY_YES("http://purl.org/ontology/breast_cancer_recommendation#UY_chest_radiotherapy_yes_value"),
    UY_CHEST_RADIOTERAPHY_NO("http://purl.org/ontology/breast_cancer_recommendation#UY_chest_radiotherapy_no_value"),
    UY_HIPERPLASIA_ATIPIA_NO("http://purl.org/ontology/breast_cancer_recommendation#UY_hiperplasia_atipia_no_value"),
    UY_HIPERPLASIA_ATIPIA_YES("http://purl.org/ontology/breast_cancer_recommendation#UY_hiperplasia_atipia_yes_value");

    private final String uri;
}

