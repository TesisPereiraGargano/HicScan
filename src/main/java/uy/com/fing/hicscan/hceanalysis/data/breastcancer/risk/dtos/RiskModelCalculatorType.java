package uy.com.fing.hicscan.hceanalysis.data.breastcancer.risk.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RiskModelCalculatorType {
    IBIS_IKONOPEDIOA_WEB_SCRAPPER("IBIS - ibis.ikonopedia.com web scrapper"),
    MSP_UY_GUIDELINE("MSP - UY Guideline"),
    ACS_GUIDELINE("ACS - Guideline");

    private final String description;
}

