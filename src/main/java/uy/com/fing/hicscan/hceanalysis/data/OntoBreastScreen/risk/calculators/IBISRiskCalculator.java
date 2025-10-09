package uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.calculators;

import org.springframework.stereotype.Component;

import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.dtos.RiskCalculation;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.dtos.RiskLevel;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.dtos.RiskModel;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.dtos.RiskModelCalculatorType;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.exception.RiskCalculatorException;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.scraper.IBISIkonopediaWebScrapper;

import java.util.Map;

/**
 * Calculadora de IBIS siguiendo web scraper de Ikonopedia.
 */
@Component
public class IBISRiskCalculator extends RiskCalculator {

    private final IBISIkonopediaWebScrapper ibisIkonopediaWebScrapper;

    public IBISRiskCalculator(IBISIkonopediaWebScrapper ibisIkonopediaWebScrapper) {
        this.ibisIkonopediaWebScrapper = ibisIkonopediaWebScrapper;
    }

    @Override
    public RiskCalculation doRiskCalculation(Map<String, String> womanFormData) {
        try{
                return ibisIkonopediaWebScrapper.calculateRisk(womanFormData);
        } catch (RiskCalculatorException e) {
            //TODO: throw 429 en caso de detectar el máximo cantidad de usos.
            //TODO: debemos elevar la excepción y hacer algo a nivel de front??.
            return RiskCalculation.builder()
                    .riskLevel(RiskLevel.MEDIUM)
                    .riskModelCalculatorType(getRiskModelCalculator())
                    .riskModel(getRiskModel())
                    .build();
        }
    }

    @Override
    public RiskModelCalculatorType getRiskModelCalculator() {
        return RiskModelCalculatorType.IBIS_IKONOPEDIOA_WEB_SCRAPPER;
    }

    @Override
    public RiskModel getRiskModel() {
        return RiskModel.IBIS;
    }

}
