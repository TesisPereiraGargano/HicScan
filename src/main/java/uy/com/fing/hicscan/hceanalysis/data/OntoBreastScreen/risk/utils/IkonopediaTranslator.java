package uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IkonopediaTranslator {

    public static String mapBreastDensity(String valueUri) {
        switch (valueUri){
            case "http://purl.org/ontology/breast_cancer_recommendation#IBIS_breast_density_heterog_dense_value" -> {
                return "Heterogeneously Dense";
            }
            case "http://purl.org/ontology/breast_cancer_recommendation#IBIS_breast_density_fatty_value" -> {
                return "Fatty";
            }
            case "http://purl.org/ontology/breast_cancer_recommendation#IBIS_breast_density_extrem_dense_value" -> {
                return "Extremely Dense";
            }
            default -> {
                return "Average";
            }
        }
    }

    public static String mapMenopauseStatus(String valueUri) {
        switch (valueUri){
            case "http://purl.org/ontology/breast_cancer_recommendation#IBIS_has_menopause_no_value" -> {
                return "No";
            }
            case "http://purl.org/ontology/breast_cancer_recommendation#IBIS_has_menopause_yes_value" -> {
                return "Yes";
            }
            case "http://purl.org/ontology/breast_cancer_recommendation#IBIS_has_menopause_now_value" -> {
                return "In Menopause Now";
            }
            default -> {
                return "Don't Know";
            }
        }
    }

    public static String mapHrt(String valueUri) {
        switch (valueUri){
            case "http://purl.org/ontology/breast_cancer_recommendation#IBIS_horm_therapy_now_value" -> {
                return "Current User";
            }
            case "http://purl.org/ontology/breast_cancer_recommendation#IBIS_horm_therapy_never_value" -> {
                return "Never";
            }
            case "http://purl.org/ontology/breast_cancer_recommendation#IBIS_horm_therapy_5_years_more_value" -> {
                return "Stopped use 5 or more years ago";
            }
            default -> {
                return "Stopped use less than 5 years ago";
            }
        }
    }

    public static String mapWomanBRCA(String valueUri) {
        switch (valueUri){
            case "http://purl.org/ontology/breast_cancer_recommendation#IBIS_BRCA_gen_BRCA1_value" -> {
                return "BRCA1+";
            }
            case "http://purl.org/ontology/breast_cancer_recommendation#IBIS_BRCA_gen_BRCA2_value" -> {
                return "BRCA2+";
            }
            case "http://purl.org/ontology/breast_cancer_recommendation#IBIS_BRCA_gen_test_normal_value" -> {
                return "Tested, Normal";
            }
            default -> {
                return "Unknown";
            }
        }
    }

    public static String mapMotherBRCA(String valueUri) {
        switch (valueUri){
            case "http://purl.org/ontology/breast_cancer_recommendation#IBIS_BRCA_gen_mother_BRCA1_value" -> {
                return "BRCA1+";
            }
            case "http://purl.org/ontology/breast_cancer_recommendation#IBIS_BRCA_gen_mother_BRCA2_value" -> {
                return "BRCA2+";
            }
            case "http://purl.org/ontology/breast_cancer_recommendation#IBIS_BRCA_gen_mother_test_normal_value" -> {
                return "Tested, Normal";
            }
            default -> {
                return "Unknown";
            }
        }
    }

    public static String mapSisterBRCA(String valueUri) {
        switch (valueUri){
            case "http://purl.org/ontology/breast_cancer_recommendation#IBIS_sister_BRCA_gen_BRCA1_value" -> {
                return "BRCA1+";
            }
            case "http://purl.org/ontology/breast_cancer_recommendation#IBIS_sister_BRCA_gen_BRCA2_value" -> {
                return "BRCA2+";
            }
            case "http://purl.org/ontology/breast_cancer_recommendation#IBIS_sister_BRCA_gen_test_normal_value" -> {
                return "Tested, Normal";
            }
            default -> {
                return "Unknown";
            }
        }
    }

    public static String mapBCBiopsy(String valueUri) {
        switch (valueUri){
            case "http://purl.org/ontology/breast_cancer_recommendation#IBIS_breast_biopsy_unknown_value" -> {
                return "Prior biopsy, result unknown";
            }
            case "http://purl.org/ontology/breast_cancer_recommendation#IBIS_breast_biopsy_without_value" -> {
                return "No prior biopsy / no proliferative disease";
            }
            case "http://purl.org/ontology/breast_cancer_recommendation#IBIS_breast_biopsy_hiperplasia_atipica_value" -> {
                return "Atypical Hyperplasia";
            }
            case "http://purl.org/ontology/breast_cancer_recommendation#IBIS_breast_biopsy_hiperplasia_no_atipia_value" -> {
                return "Hyperplasia (not atypia)";
            }
            default -> {
                return "Lobular Carcinoma in Situ (LCIS)";
            }
        }
    }

    public static String mapYesNo(String value) {
        return value.contains("_yes_value") ? "Yes" : "No";
    }
}

