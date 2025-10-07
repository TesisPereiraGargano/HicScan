package uy.com.fing.hicscan.hceanalysis.data.breastcancer.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.ontology.Individual;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class LanguageLabelUtils {

    public static String getLabelInLanguageOrDefault(Individual individual, String language) {
        String labelInPrimaryLanguage = individual.getLabel(language);

        if(labelInPrimaryLanguage == null) {
            log.warn("Label of {} for lang [{}] is <null>, using fallback any-language label", individual.getURI(), language);
            return individual.getLabel(null);
        } else {
            log.info("Label of {} for lang [{}] found and is {}", individual.getURI(), language, labelInPrimaryLanguage);
        }

        return labelInPrimaryLanguage;
    }
}

