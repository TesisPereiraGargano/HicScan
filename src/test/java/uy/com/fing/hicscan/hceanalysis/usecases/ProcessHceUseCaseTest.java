package uy.com.fing.hicscan.hceanalysis.usecases;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import uy.com.fing.hicscan.hceanalysis.adapters.HCEAdapterFactory;
import uy.com.fing.hicscan.hceanalysis.data.plainTextProcessor.PlainTextProcessor;
import uy.com.fing.hicscan.hceanalysis.data.translator.Translator;
import uy.com.fing.hicscan.hceanalysis.dto.PacienteBasico;
import uy.com.fing.hicscan.hceanalysis.dto.PacienteExtendido;
import uy.com.fing.hicscan.hceanalysis.languageexpansion.LanguageExpansion;
import uy.com.fing.hicscan.hceanalysis.utils.GestionDocumentosHCE;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class ProcessHceUseCaseTest {

    private static final String TEST_ID = "TEST-HCE-1";

    // Mismo contenido de HCE utilizado en HL7v3_CDAr2_AdapterTest
    private final String xmlContent =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<ClinicalDocument xmlns=\"urn:hl7-org:v3\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"urn:hl7-org:v3 POCD_MT000040.xsd\">\n" +
            "  <realmCode code=\"urn:oid:2.16.840.1.113883.6.1\"/>\n" +
            "  <typeId root=\"2.16.840.1.113883.1.3\" extension=\"POCD_HD000040\"/>\n" +
            "  <templateId root=\"2.16.840.1.113883.10.20.1\"/>\n" +
            "  <id extension=\"TEST-1\"/>\n" +
            "  <code code=\"34133-9\" codeSystem=\"2.16.840.1.113883.6.1\" displayName=\"Summarization of Episode Note\"/>\n" +
            "  <title>Documento de Prueba</title>\n" +
            "  <effectiveTime value=\"20250927\"/>\n" +
            "  <recordTarget>\n" +
            "    <patientRole>\n" +
            "      <patient>\n" +
            "        <name>\n" +
            "          <family>Rodriguez</family>\n" +
            "          <family>Martinez</family>\n" +
            "          <given>Juana</given>\n" +
            "        </name>\n" +
            "        <administrativeGenderCode code=\"F\" />\n" +
            "        <birthTime value=\"19721118\" />\n" +
            "        <raceCode code=\"2106-3\" />\n" +
            "        <birthplace classCode=\"BIRTHPL\" >\n" +
            "          <place classCode=\"PLC\" determinerCode=\"INSTANCE\">\n" +
            "               <name>Hospital</name>\n" +
            "               <addr>Montevideo, Uruguay</addr>\n" +
            "          </place>\n" +
            "        </birthplace>\n" +
            "        <maritalStatusCode code=\"M\" />\n" +
            "      </patient>\n" +
            "    </patientRole>\n" +
            "  </recordTarget>\n" +
            "  <author>\n" +
            "    <time value=\"20250927\"/>\n" +
            "    <assignedAuthor>\n" +
            "      <id root=\"1.2.3.4.5.6\"/>\n" +
            "      <assignedPerson>\n" +
            "        <name>Dr. Pedro Perez</name>\n" +
            "      </assignedPerson>\n" +
            "      <representedOrganization>\n" +
            "        <name>Hospital Prueba</name>\n" +
            "      </representedOrganization>\n" +
            "    </assignedAuthor>\n" +
            "  </author>\n" +
            "  <component>\n" +
            "    <structuredBody>\n" +
            "      <component>\n" +
            "        <section>\n" +
            "          <code code=\"10160-0\" codeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\"/>\n" +
            "          <title>Medications</title>\n" +
            "          <text>\n" +
            "            <list>\n" +
            "              <item>Theodur 200mg BID</item>\n" +
            "            </list>\n" +
            "          </text>\n" +
            "          <entry>\n" +
            "            <substanceAdministration classCode=\"SBADM\" moodCode=\"EVN\">\n" +
            "              <text>Theodur 200mg BID</text>\n" +
            "              <effectiveTime xsi:type=\"PIVL_TS\" institutionSpecified=\"true\">\n" +
            "                <period value=\"12\" unit=\"h\"/>\n" +
            "              </effectiveTime>\n" +
            "              <routeCode code=\"PO\" codeSystem=\"2.16.840.1.113883.5.112\" codeSystemName=\"RouteOfAdministration\"/>\n" +
            "              <doseQuantity value=\"200\" unit=\"mg\"/>\n" +
            "              <consumable>\n" +
            "                <manufacturedProduct>\n" +
            "                  <manufacturedLabeledDrug>\n" +
            "                    <code code=\"66493003\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"Theophylline\"/>\n" +
            "                  </manufacturedLabeledDrug>\n" +
            "                </manufacturedProduct>\n" +
            "              </consumable>\n" +
            "            </substanceAdministration>\n" +
            "          </entry>\n" +
            "        </section>\n" +
            "      </component>\n" +
            "      <component>\n" +
            "        <section>\n" +
            "          <text>Este es el texto libre del informe.</text>\n" +
            "          <text>Se puede concatenar todo.</text>\n" +
            "        </section>\n" +
            "      </component>\n" +
            "     <component>\n" +
            "        <section>\n" +
            "            <code code=\"11384-5\" codeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\"/>\n" +
            "            <title>Examen Fisico</title>\n" +
            "            <component>\n" +
            "                <section>\n" +
            "                    <code code=\"8716-3\" codeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\"/>\n" +
            "                    <title>Vital Signs</title>\n" +
            "                    <entry>\n" +
            "                        <observation classCode=\"OBS\" moodCode=\"EVN\">\n" +
            "                            <code code=\"50373000\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"Medicion de altura\"/>\n" +
            "                            <statusCode code=\"completed\"/>\n" +
            "                            <effectiveTime value=\"200004071430\"/>\n" +
            "                            <value xsi:type=\"PQ\" value=\"1.77\" unit=\"m\"></value>\n" +
            "                        </observation>\n" +
            "                    </entry>\n" +
            "                    <entry>\n" +
            "                        <observation classCode=\"OBS\" moodCode=\"EVN\">\n" +
            "                            <code code=\"363808001\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"Body weight measure\"/>\n" +
            "                            <statusCode code=\"completed\"/>\n" +
            "                            <effectiveTime value=\"200004071430\"/>\n" +
            "                            <value xsi:type=\"PQ\" value=\"88.0\" unit=\"kg\"></value>\n" +
            "                        </observation>\n" +
            "                    </entry>\n" +
            "                </section>\n" +
            "            </component>\n" +
            "        </section>\n" +
            "      </component>\n" +
            "    </structuredBody>\n" +
            "  </component>\n" +
            "</ClinicalDocument>";

    private ProcessHceUseCase buildUseCase() {
        PlainTextProcessor plainTextProcessor = mock(PlainTextProcessor.class);
        Translator translator = mock(Translator.class);
        LanguageExpansion languageExpansion = mock(LanguageExpansion.class);
        HCEAdapterFactory adapterFactory = new HCEAdapterFactory();
        InstanciateOntology instanciateOntology = mock(InstanciateOntology.class);

        return new ProcessHceUseCase(plainTextProcessor, translator, languageExpansion, adapterFactory, instanciateOntology);
    }

    @AfterEach
    void cleanup() {
        GestionDocumentosHCE.eliminarDocumento(TEST_ID);
    }

    @Test
    void obtenerDatosPaciente_debeRetornarPacienteExtendidoDesdeCda() {
        GestionDocumentosHCE.guardarDocumento(TEST_ID, xmlContent);

        ProcessHceUseCase useCase = buildUseCase();

        PacienteExtendido paciente = useCase.obtenerDatosPaciente(TEST_ID);

        assertNotNull(paciente);
        assertEquals("RodriguezMartinezJuana", paciente.getNombre().replaceAll("\\s+", ""));
        assertEquals("F", paciente.getGenero());
        assertEquals("19721118", paciente.getFechaNacimiento());
        assertEquals("2106-3", paciente.getRaza());
        assertEquals("Montevideo, Uruguay", paciente.getLugarNacimiento());
        assertEquals("M", paciente.getEstadoCivil());
        assertEquals("1.77", paciente.getAlturaValor());
        assertEquals("m", paciente.getAlturaUnidad());
        assertEquals("88.0", paciente.getPesoValor());
        assertEquals("kg", paciente.getPesoUnidad());
    }

    @Test
    void obtenerDatosPaciente_debeRetornarNullSiNoExisteDocumento() {
        ProcessHceUseCase useCase = buildUseCase();

        PacienteExtendido paciente = useCase.obtenerDatosPaciente("ID-INEXISTENTE");

        assertNull(paciente);
    }

    @Test
    void obtenerTodosLosPacientesBasicos_debeIncluirPacienteBasicoParaHceGuardada() {
        GestionDocumentosHCE.guardarDocumento(TEST_ID, xmlContent);

        ProcessHceUseCase useCase = buildUseCase();

        List<PacienteBasico> pacientes = useCase.obtenerTodosLosPacientesBasicos();

        PacienteBasico encontrado = pacientes.stream()
                .filter(p -> TEST_ID.equals(p.getId()))
                .findFirst()
                .orElse(null);

        assertNotNull(encontrado);
        assertEquals("RodriguezMartinezJuana", encontrado.getNombre().replaceAll("\\s+", ""));
        assertEquals("F", encontrado.getGenero());
        assertEquals("19721118", encontrado.getFechaNacimiento());
        assertEquals("2106-3", encontrado.getRaza());
        assertEquals("Montevideo, Uruguay", encontrado.getLugarNacimiento());
        assertEquals("M", encontrado.getEstadoCivil());
        assertEquals("1.77", encontrado.getAlturaValor());
        assertEquals("m", encontrado.getAlturaUnidad());
        assertEquals("88.0", encontrado.getPesoValor());
        assertEquals("kg", encontrado.getPesoUnidad());
    }
}


