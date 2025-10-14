package uy.com.fing.hicscan.hceanalysis.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;
import uy.com.fing.hicscan.hceanalysis.data.breastcancer.recommend.WomanRecommendation;
import uy.com.fing.hicscan.hceanalysis.data.breastcancer.risk.WomanRisk;
import uy.com.fing.hicscan.hceanalysis.usecases.BreastCancerStudiesUseCase;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/breast-cancer/v1")
public class BreastCancerController {

    private BreastCancerStudiesUseCase breastCancerStudiesUseCase;

    public record CalculateRiskFormData(Map<String, String> womanHistory, String riskModelUri) {}

    @PostMapping("/woman")
    public WomanRisk submitWomanFormAndCalculateRisk(@RequestBody CalculateRiskFormData data) {
        String language = LocaleContextHolder.getLocale().getLanguage();
        log.info("Comenzando a calcular el riesgo de contraer cancer de mama con los siguientes parámetros {} y language {}", data, language);

        WomanRisk womanRisk = breastCancerStudiesUseCase.calculateRiskAndCreateWoman(data.riskModelUri(), data.womanHistory(), language);

        log.info("Finalizando calculo del riesgo de contraer cancer de mama con los siguientes parámetros {}, language {} y resultado {}",
                data, language, womanRisk);
        return womanRisk;
    }

    @GetMapping("/recommendation_guides")
    public List<BreastCancerStudiesUseCase.IndividualDescriptor> getRecommendationGuides(@RequestParam("risk") String riskLevelUri) {
        String language = LocaleContextHolder.getLocale().getLanguage();
        log.info("Comenzando a obtener las guías de recomendación de estudios para el nivel {} y lenguage {}", riskLevelUri, language);

        List<BreastCancerStudiesUseCase.IndividualDescriptor> guidelines = breastCancerStudiesUseCase.getAllGuidelinesFor(riskLevelUri, language);

        log.info("Finalizando obtención de las guias de recommendación de estudios con nivel {} y lenguaje {} con resultado {}", riskLevelUri, language,
                guidelines);
        return guidelines;
    }

    @GetMapping("/recommendation")
    public WomanRecommendation getWomanRecommendation(@RequestParam("womanId") String womanId, @RequestParam("guidelineUri") String guidelineUri) {
        String language = LocaleContextHolder.getLocale().getLanguage();
        log.info("Comenzando a obtener la recomendación de estudios para la mujer {} y lenguage {}", womanId, language);

        WomanRecommendation womanRecommendation = breastCancerStudiesUseCase.getWomanAllRecommendations(womanId, guidelineUri, language);

        log.info("Finalizando obtención de recomendación de estudios para la mujer {} y lenguage {} con resultado {}", womanId, language,
                womanRecommendation);
        return womanRecommendation;
    }

}

