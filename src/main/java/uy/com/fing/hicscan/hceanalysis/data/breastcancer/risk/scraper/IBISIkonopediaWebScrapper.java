package uy.com.fing.hicscan.hceanalysis.data.breastcancer.risk.scraper;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.springframework.stereotype.Service;
import uy.com.fing.hicscan.hceanalysis.data.breastcancer.risk.dtos.RiskCalculation;
import uy.com.fing.hicscan.hceanalysis.data.breastcancer.risk.dtos.RiskLevel;
import uy.com.fing.hicscan.hceanalysis.data.breastcancer.risk.dtos.RiskModel;
import uy.com.fing.hicscan.hceanalysis.data.breastcancer.risk.dtos.RiskModelCalculatorType;
import uy.com.fing.hicscan.hceanalysis.data.breastcancer.risk.exception.RiskCalculatorException;

import java.util.List;
import java.util.Map;

import static uy.com.fing.hicscan.hceanalysis.data.breastcancer.ontology.BCRQuestionInstancesEnum.*;
import static uy.com.fing.hicscan.hceanalysis.data.breastcancer.risk.utils.IkonopediaTranslator.*;

@Service
@Slf4j
public class IBISIkonopediaWebScrapper {

    private static final String YES = "Yes";
    private ThreadLocal<WebDriver> driverThreadLocal;

    public RiskCalculation calculateRisk(Map<String, String> d) throws RiskCalculatorException {
        WebDriver driver = prepareDriver();

        double riskAllLife;
        double riskTenYears;

        driverThreadLocal = ThreadLocal.withInitial(() -> driver);

        try {
            // Navigate to the IBIS Risk Assessment Tool
            driver.get("https://ibis.ikonopedia.com");
            Thread.sleep(500);

            //Define System of Measurement.
            findElementRadioAndClick("TCEnglishSystem", "Metric Units");

            //Define Breast Density Measure
            findElementRadioAndClick("TCDensityMeasure", "BI-RADS® ATLAS*");

            //PERSONAL HISTORY
            String age = d.get(IBIS_PERSONAL_AGE_QUESTION.getUri());
            if(age != null) {
                sendKeysElement("CurrentAge", age);
            }


            String weight = d.get(IBIS_PERSONAL_WEIGHT_QUESTION.getUri());
            if(weight != null) {
                sendKeysElement("WeightKg", weight);
            }

            String height = d.get(IBIS_PERSONAL_HEIGHT_QUESTION.getUri());
            if(height != null) {
                sendKeysElement("HeightM", height);
            }


            String menarche = d.get(IBIS_HORMONAL_MENARCHE_AGE.getUri());
            if(menarche != null)
                sendKeysElement("TCMenarcheAge", menarche);

            //Define Breast Density
            String breastDensity = d.get(IBIS_PERSONAL_BRAST_DENSITY.getUri());
            if(breastDensity != null)
                findElementRadioAndClick("TCDensityBirads", mapBreastDensity(breastDensity));

            //Define children birth
            String childsValue = d.get(IBIS_HORMONAL_CHILDS.getUri());
            if(childsValue != null) {
                String hasChilds = mapYesNo(childsValue);
                findElementRadioAndClick("TCParous", hasChilds);

                if(YES.equals(hasChilds)) {
                    String childsAge = d.get(IBIS_HORMONAL_CHILDS_AGE.getUri());
                    if(childsAge != null)
                        sendKeysElement("TCFirstLiveBirthAge", childsAge);
                }
            }

            //Define gone through menopause
            String menopauseVal = d.get(IBIS_HORMONAL_MENOPAUSE.getUri());
            if(menopauseVal != null) {
                String menopauseStatus = mapMenopauseStatus(menopauseVal);
                findElementRadioAndClick("TCMenopauseStatus", menopauseStatus);

                if(YES.equals(menopauseStatus)) {
                    String menopauseAge = d.get(IBIS_HORMONAL_MENOPAUSE_AGE.getUri());
                    if(menopauseAge != null)
                        sendKeysElement("TCMenopauseAge", menopauseAge);
                }
            }

            //Define hormone replacement therapy
            String hrtValue = d.get(IBIS_HORMONAL_HRT.getUri());
            if(hrtValue != null) {
                String hrtUse = mapHrt(hrtValue);
                findElementRadioAndClick("TCHRTUse", hrtUse);

                if(hrtUse.equals("Stopped use less than 5 years")) {
//                findElementRadioAndClick("TCHRTType", hrtType);
//                findElementRadioAndClick("TCHRTLength", hrtLength);
//                findElementRadioAndClick("TCHRTLastUse", hrtLastUse);
                }

                if(hrtUse.equals("Current User")) {
//                findElementRadioAndClick("TCHRTType", hrtType);
//                findElementRadioAndClick("TCHRTLength", hrtLength);
//                findElementRadioAndClick("TCHRTIntendedUseLength", hrtIntendedUseLength);
                }
            }


            //define BRCA Gene
            String genBRCAValue = d.get(IBIS_GEN_BRCA.getUri());
            if(genBRCAValue != null)
                findElementRadioAndClick("TCBRCA", mapWomanBRCA(genBRCAValue));

            //define Ovarian Cancer
            String hasOC = d.get(IBIS_OVARIAN_CANCER.getUri());
            if(hasOC != null) {
                findElementRadioAndClick("TCOvarianCancer", mapYesNo(hasOC));

                if(YES.equals(hasOC)) {
                    String ocAge = d.get(IBIS_OVARIAN_CANCER_AGE.getUri());
                    if(ocAge != null)
                        sendKeysElement("TCOvarianCancerAge", ocAge);
                }
            }

            //define Breast biopsy
            String breastDisVal = d.get(IBIS_BREAST_DISEASE.getUri());
            if(breastDisVal != null)
                findElementRadioAndClick("SpecialBiopsyQuestions", mapBCBiopsy(breastDisVal));

            //Define Ashkenazi inheritance
            String ashkenaziVa = d.get(IBIS_FAMILY_ASHKENAZI.getUri());
            if(ashkenaziVa != null)
                findElementRadioAndClick("TCAshkenazi", mapYesNo(ashkenaziVa));


            if(d.get(IBIS_MOTHER_AGE.getUri()) != null) {

                clickAddFamilySectionFor("Mother");

                sendKeysElement("TCAge_Mother", d.get(IBIS_MOTHER_AGE.getUri()));
                String hasMotherBC = mapYesNo(d.get(IBIS_MOTHER_BC.getUri()));
                findElementRadioAndClick("TCAffected_Mother", hasMotherBC);

                if(YES.equals(hasMotherBC)) {

                    sendKeysElement("TCAffectedCancerAge_Mother", d.get(IBIS_MOTHER_BC_START.getUri()));
                    String hasMotherBilateral = mapYesNo(d.get(IBIS_MOTHER_BC_BILATERAL.getUri()));
                    findElementRadioAndClick("TCBilateral_Mother", hasMotherBilateral);

                    if(YES.equals(hasMotherBilateral)) {
                        sendKeysElement("TCBilateralAge_Mother", d.get(IBIS_MOTHER_BC_BILATERAL_AGE.getUri()));
                    }
                }

                String hasMotherOC = mapYesNo(d.get(IBIS_MOTHER_OVARIAN.getUri()));
                findElementRadioAndClick("TCOvarian_Mother", hasMotherOC);

                if(YES.equals(hasMotherOC)) {
                    sendKeysElement("TCOvarianAge_Mother", d.get(IBIS_MOTHER_OVARIAN_AGE.getUri()));
                }

                findElementRadioAndClick("TCBRCA_Mother", mapMotherBRCA(d.get(IBIS_MOTHER_BRCA.getUri())));

            }

            if(d.get(IBIS_SISTER_AGE.getUri()) != null ) {

                //Add Family button.
                clickAddFamilySectionFor("Sister");


                sendKeysElement("TCAge_Sister_", d.get(IBIS_SISTER_AGE.getUri()));
                String sisterHasBC = mapYesNo(d.get(IBIS_SISTER_BC.getUri()));
                findElementRadioAndClick("TCAffected_Sister_", sisterHasBC);

                if(YES.equals(sisterHasBC)) {

                    sendKeysElement("TCAffectedCancerAge_Sister_", d.get(IBIS_SISTER_BC_START.getUri()));
                    String sisterHasBilateral = mapYesNo(d.get(IBIS_SISTER_BC_BILATERAL.getUri()));
                    findElementRadioAndClick("TCBilateral_Sister_", sisterHasBilateral);

                    if(YES.equals(sisterHasBilateral)) {
                        sendKeysElement("TCBilateralAge_Sister_", d.get(IBIS_SISTER_BC_BILATERAL_AGE.getUri()));
                    }
                }

                String hasSisterOC = mapYesNo(d.get(IBIS_SISTER_OVARIAN.getUri()));
                findElementRadioAndClick("TCOvarian_Sister_", hasSisterOC);

                if(YES.equals(hasSisterOC)) {
                    sendKeysElement("TCOvarianAge_Sister_", d.get(IBIS_SISTER_OVARIAN_AGE.getUri()));
                }

                findElementRadioAndClick("TCBRCA_Sister_", mapSisterBRCA(d.get(IBIS_SISTER_BRCA.getUri())));
            }


            //Apretar el botòn de calcular.
            driver.findElement(By.id("calc_button")).click();

            Thread.sleep(500);

            WebElement tcRiskTenYear = driver.findElement(By.id("tc_risk_ten_year"));
            String womanTenYearRisk = tcRiskTenYear.findElements(By.tagName("strong")).get(0).getText();

            WebElement lifeTimeRisk = driver.findElement(By.id("tc_lifetime"));
            String womanLifeTimeRisk = lifeTimeRisk.findElements(By.tagName("strong")).get(0).getText();

            log.info("El riesgo para la mujer es 10 años {}, toda la vida {} ", womanTenYearRisk, womanLifeTimeRisk);

            riskAllLife = Double.parseDouble(womanLifeTimeRisk.replace("%", ""));
            riskTenYears = Double.parseDouble(womanTenYearRisk.replace("%", ""));

        } catch (Exception e) {
            String msg = String.format("No se pudo calcular el riesgo utilizando la estrategia IBISWebScrapper debido a %s", e.getMessage());
            log.error(msg, e);
            throw new RiskCalculatorException(msg, e);
        } finally {
           driver.quit();
        }

        var riskLevel = riskAllLife > 20d ? RiskLevel.HIGH : RiskLevel.MEDIUM;
        return RiskCalculation.builder()
                .riskAllLife(riskAllLife)
                .riskTenYears(riskTenYears)
                .riskModelCalculatorType(RiskModelCalculatorType.IBIS_IKONOPEDIOA_WEB_SCRAPPER)
                .riskModel(RiskModel.IBIS)
                .riskLevel(riskLevel)
                .build();
    }



    private void clickAddFamilySectionFor(String familyMember) throws InterruptedException {
        WebDriver driver = driverThreadLocal.get();
        Actions actions = new Actions(driver);
        WebElement btnFamily = driver.findElement(By.id("family_hx_btn"));
        actions.moveToElement(btnFamily).perform();
        Thread.sleep(500);
        btnFamily.click();

        WebElement wra = driver.findElement(By.id("ik_dropbox_wrapper"));
        List<WebElement> trs = wra.findElements(By.tagName("tr"));

        for (WebElement elem: trs) {
            if(elem.getText().equals(familyMember)){
                elem.click();
                break;
            }
        }
    }

    private void sendKeysElement(String elementId, Object value) throws InterruptedException {
        String keysToSend = "";

        if(value instanceof String) {
            keysToSend = (String) value;
        } else if (value instanceof Boolean booleanVal) {
            keysToSend = booleanVal? YES : "No";
        } else if (value instanceof Integer intVal) {
            keysToSend = String.valueOf(intVal);
        } else if (value instanceof Double doubleVal) {
            keysToSend = String.valueOf(doubleVal);
        }


        log.info("try to define {} with value {}", elementId, keysToSend);
        WebDriver driver = driverThreadLocal.get();
        Actions actions = new Actions(driver);

        WebElement element = driver.findElement(By.id(elementId));

        actions.moveToElement(element).perform();
        Thread.sleep(200);

        element.sendKeys(keysToSend);
    }

    private void findElementRadioAndClick(String elementId, Object value) throws InterruptedException {
        String radioText = "";

        if(value instanceof String) {
            radioText = (String) value;
        } else if (value instanceof Boolean booleanVal) {
            radioText = booleanVal? YES : "No";
        }

        log.info("try to define {} with value {}", elementId, radioText);
        WebDriver driver = driverThreadLocal.get();
        Actions actions = new Actions(driver);

        WebElement element = driver.findElement(
                By.xpath("//ul[@id='"+ elementId + "']/li[contains(text(), '"+ radioText +"')] | " +
                        "//ol[@id='"+ elementId + "']/li[contains(text(), '"+ radioText +"')]"));

        actions.moveToElement(element).perform();

        Thread.sleep(500); //TODO deberìa ser sustituido por un expectcondition.
        element.click();
        //assert element.isSelected();
    }


    private WebDriver prepareDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--ignore-ssl-errors=yes");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--headless"); // Run Chrome in headless mode
        options.addArguments("--no-sandbox");  // Para Docker
        options.addArguments("--disable-dev-shm-usage");  // Para Docker
        options.setBinary("/usr/bin/google-chrome");

        return new ChromeDriver(options);
    }



}

