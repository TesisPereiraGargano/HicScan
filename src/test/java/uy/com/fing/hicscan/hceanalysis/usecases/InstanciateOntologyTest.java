package uy.com.fing.hicscan.hceanalysis.usecases;

import org.apache.jena.ontology.OntModel;
import org.junit.jupiter.api.Test;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontology.OntologyRepository;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontoforms.OntoFormsClient;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.recommend.WomanRecommendation;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.WomanRisk;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.dtos.RiskModel;
import uy.com.fing.hicscan.hceanalysis.data.ontologyRepository.OntologyOperations;
import uy.com.fing.hicscan.hceanalysis.data.ontologyRepository.ReasoningResult;
import uy.com.fing.hicscan.hceanalysis.dto.DatosHCE;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class InstanciateOntologyTest {

    @Test
    void processWomanWithMedicationAndReasoning_happyPath_returnsResultWithRecommendations() {
        OntologyRepository ontologyRepository = mock(OntologyRepository.class);
        OntoFormsClient ontoFormsClient = mock(OntoFormsClient.class);
        OntologyOperations ontologyOperations = mock(OntologyOperations.class);
        BreastCancerStudiesUseCase breastCancerStudiesUseCase = mock(BreastCancerStudiesUseCase.class);

        InstanciateOntology instanciateOntology =
                new InstanciateOntology(ontologyRepository, ontoFormsClient, ontologyOperations, breastCancerStudiesUseCase);

        OntModel mockModel = mock(OntModel.class);
        when(ontoFormsClient.getOntologyFileName()).thenReturn("test-onto");
        when(ontologyRepository.getOntologyModelABoxByIdFor("test-onto")).thenReturn(mockModel);

        WomanRisk womanRisk = WomanRisk.builder()
                .womanUri("http://example.org/woman/1")
                .riskLevelUri("http://example.org/risk/level")
                .riskLevel("Medium")
                .riskCalculation(null)
                .build();
        when(breastCancerStudiesUseCase.calculateRiskAndCreateWoman(
                eq(mockModel),
                anyString(),
                anyMap(),
                anyString()))
                .thenReturn(womanRisk);

        BreastCancerStudiesUseCase.IndividualDescriptor guidelineDescriptor =
                new BreastCancerStudiesUseCase.IndividualDescriptor("http://example.org/guideline/1", "Guide 1");
        when(breastCancerStudiesUseCase.getAllGuidelinesFor(eq(womanRisk.getRiskLevelUri()), anyString()))
                .thenReturn(List.of(guidelineDescriptor));

        WomanRecommendation womanRecommendation = WomanRecommendation.builder().build();
        when(breastCancerStudiesUseCase.getWomanAllRecommendations(
                eq(mockModel),
                eq(womanRisk.getWomanUri()),
                eq(guidelineDescriptor.uri()),
                anyString()))
                .thenReturn(womanRecommendation);

        ReasoningResult baseReasoning = new ReasoningResult(
                Collections.singletonList("stmt"), Collections.emptyList(), 1, true, null, null);
        when(ontologyOperations.executeReasoner(mockModel)).thenReturn(baseReasoning);

        DatosHCE datosHCE = new DatosHCE();
        Map<String, String> historyData = Map.of("key", "value");

        ReasoningResult result = instanciateOntology.processWomanWithMedicationAndReasoning(
                RiskModel.MSP_UY,
                historyData,
                datosHCE);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getTotalStatements());
        assertEquals(1, result.getDerivedStatements().size());
        assertNotNull(result.getWomanRecommendation());

        verify(ontologyOperations).createMedicationsFromHCE(mockModel, womanRisk.getWomanUri(), datosHCE);
        verify(ontologyOperations).executeReasoner(mockModel);
        verify(mockModel).close();
    }

    @Test
    void processWomanWithMedicationAndReasoning_whenOntologyModelIsNull_returnsFailedResult() {
        OntologyRepository ontologyRepository = mock(OntologyRepository.class);
        OntoFormsClient ontoFormsClient = mock(OntoFormsClient.class);
        OntologyOperations ontologyOperations = mock(OntologyOperations.class);
        BreastCancerStudiesUseCase breastCancerStudiesUseCase = mock(BreastCancerStudiesUseCase.class);

        InstanciateOntology instanciateOntology =
                new InstanciateOntology(ontologyRepository, ontoFormsClient, ontologyOperations, breastCancerStudiesUseCase);

        when(ontoFormsClient.getOntologyFileName()).thenReturn("test-onto");
        when(ontologyRepository.getOntologyModelABoxByIdFor("test-onto")).thenReturn(null);

        ReasoningResult result = instanciateOntology.processWomanWithMedicationAndReasoning(
                RiskModel.MSP_UY,
                Collections.emptyMap(),
                new DatosHCE());

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(0, result.getTotalStatements());
        assertNotNull(result.getErrorMessage());

        verify(ontologyOperations, never()).createMedicationsFromHCE(any(), anyString(), any());
        verify(ontologyOperations, never()).executeReasoner(any());
    }
}


