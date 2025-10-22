package uy.com.fing.hicscan.hceanalysis.adapters.impl;

import org.junit.jupiter.api.Test;
import uy.com.fing.hicscan.hceanalysis.dto.Observacion;
import uy.com.fing.hicscan.hceanalysis.dto.Paciente;
import uy.com.fing.hicscan.hceanalysis.dto.Autor;
import uy.com.fing.hicscan.hceanalysis.dto.SustanciaAdministrada;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

    public class HL7v3_CDAr2_AdapterTest {

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
                "          <family>Rodríguez</family>\n" +
                "          <family>Martínez</family>\n" +
                "          <given>Juan</given>\n" +
                "        </name>\n" +
                "        <administrativeGenderCode code=\"M\" />\n" +
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
                "        <name>Dr. Pedro Pérez</name>\n" +
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


        @Test
        public void testParse() throws Exception {
            // Crear archivo temporal con contenido XML de prueba
            File tempFile = File.createTempFile("pruebaCDA", ".xml");
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(xmlContent);
            }

            HL7v3_CDAr2_Adapter adapter = new HL7v3_CDAr2_Adapter();
            adapter.parse(tempFile);

            Paciente paciente = adapter.getPaciente();
            Autor autor = adapter.getAutor();
            String textoLibre = adapter.getTextoLibre();
            List<SustanciaAdministrada> medicamentos = adapter.getMedicamentos();
            List<Observacion> observaciones = adapter.getObservaciones();

            assertNotNull(paciente);
            assertEquals("RodríguezMartínezJuan", paciente.getNombre().replaceAll("\\s+", ""));
            assertEquals("M", paciente.getGenero());
            assertEquals("19721118", paciente.getFechaNacimiento());
            assertEquals("2106-3", paciente.getRaza());
            assertEquals("Montevideo, Uruguay", paciente.getLugarNacimiento());
            assertEquals("M", paciente.getEstadoCivil());

            assertNotNull(autor);
            assertEquals("20250927", autor.getTime());
            assertEquals("Dr. Pedro Pérez", autor.getName());
            assertEquals("1.2.3.4.5.6", autor.getOrganizationId());
            assertEquals("Hospital Prueba", autor.getOrganizationName());

            assertNotNull(textoLibre);
            assertTrue(textoLibre.contains("Este es el texto libre del informe."));
            assertTrue(textoLibre.contains("Se puede concatenar todo."));

            assertNotNull(medicamentos);

            assertEquals("200", medicamentos.get(0).getDoseQuantityValue());
            assertEquals("mg", medicamentos.get(0).getDoseQuantityUnit());
            assertEquals("12", medicamentos.get(0).getPeriodAdministrationValue());
            assertEquals("h", medicamentos.get(0).getPeriodAdministrationUnit());
            // assertEquals("66493003", medicamentos.get(0).getDrugsCodes().get(0).getKey());
            // assertEquals("SNOMED CT", medicamentos.get(0).getDrugsCodes().get(0).getValue());

            //hay dos observaciones
            assertEquals(2, observaciones.size());
            //el primero
            Observacion obs1 = observaciones.get(0);
            assertEquals("50373000", obs1.getCode());
            assertEquals("2.16.840.1.113883.6.96", obs1.getCodeSystem());
            assertEquals("completed", obs1.getStatus());
            assertEquals("200004071430", obs1.getEffectiveTime());
            assertEquals("1.77", obs1.getMeditionValue());
            assertEquals("m", obs1.getMeditionUnit());
            //la 2da
            Observacion obs2 = observaciones.get(1);
            assertEquals("363808001", obs2.getCode());
            assertEquals("2.16.840.1.113883.6.96", obs2.getCodeSystem());
            assertEquals("completed", obs2.getStatus());
            assertEquals("200004071430", obs2.getEffectiveTime());
            assertEquals("88.0", obs2.getMeditionValue());
            assertEquals("kg", obs2.getMeditionUnit());


            Files.delete(tempFile.toPath());
        }
    }



