package uy.com.fing.hicscan.hceanalysis.usecases;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uy.com.fing.hicscan.hceanalysis.adapters.HCEAdapter;
import uy.com.fing.hicscan.hceanalysis.adapters.HCEAdapterFactory;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.dtos.RiskModel;
import uy.com.fing.hicscan.hceanalysis.data.ontologyRepository.ReasoningResult;
import uy.com.fing.hicscan.hceanalysis.data.plainTextProcessor.PlainTextProcessor;
import uy.com.fing.hicscan.hceanalysis.data.translator.Translator;
import uy.com.fing.hicscan.hceanalysis.dto.*;
import uy.com.fing.hicscan.hceanalysis.languageexpansion.LanguageExpansion;
import uy.com.fing.hicscan.hceanalysis.utils.GestionDocumentosHCE;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uy.com.fing.hicscan.hceanalysis.utils.SustanciaAdministradaUtils.*;

@Slf4j
@Service
public class ProcessHceUseCase {
    private final PlainTextProcessor plainTextProcessor;
    private final Translator translator;
    private final LanguageExpansion languajeExpansion;
    private final HCEAdapterFactory hceAdapterFactory;
    private final Map<File, HCEAdapter> adapterCache = new HashMap<>();
    private final String umlsApiKey = "9acb4127-e18e-4a0c-a53d-6555dd08fb32";
    private final InstanciateOntology instanciateOntology;


    @Value("${source.lang}")
    private String sourceLang;

    @Value("${target.lang}")
    private String targetLang;

    /**
     * Caso de uso principal para el procesamiento de Historias Clínicas Electrónicas (HCE)
     * dentro del sistema HicScan. Se encarga de extraer, traducir, clasificar y enriquecer
     * información clínica proveniente de documentos CDA/HL7, tanto estructurada como en texto libre.
     *
     * Esta clase orquesta las distintas capas del pipeline de análisis: desde la lectura del XML,
     * pasando por la expansión química/lingüística, traducción automática y obtención de códigos UMLS y RxNorm.
     *
     * Dependencias inyectadas:
     *  - {@link PlainTextProcessor}: extractor de entidades clínicas desde texto plano.
     *  - {@link Translator}: traductor de contenido médico (ej. inglés ↔ español).
     *  - {@link LanguageExpansion}: expansor de nombres comerciales a principios activos.
     *  - {@link HCEAdapterFactory}: fábrica de adaptadores para documentos HCE.
     */
    public ProcessHceUseCase (PlainTextProcessor plainTextProcessor, Translator translator, LanguageExpansion languageExpansion, HCEAdapterFactory hceAdapter, InstanciateOntology instanciateOntology){
        this.plainTextProcessor = plainTextProcessor;
        this.translator = translator;
        this.languajeExpansion = languageExpansion;
        this.hceAdapterFactory = hceAdapter;
        this.instanciateOntology = instanciateOntology;
    }

    public HCEAdapter getOrCreateAdapter(File fileHCE) {
        return adapterCache.computeIfAbsent(fileHCE, f -> {
            try {
                HCEAdapter adapter = hceAdapterFactory.getAdapter(f);
                adapter.parse(f);
                return adapter;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Extrae los datos básicos de un paciente a partir del ID de la HCE.
     * @param id identificador del documento cargado en {@link GestionDocumentosHCE}.
     * @return un objeto {@link PacienteExtendido} con los datos relevantes o {@code null} si no existe el documento.
     */
    public PacienteExtendido obtenerDatosPaciente(String id){
        if (GestionDocumentosHCE.existeDocumento(id)) {
            try {
                File tempFile = File.createTempFile("hce-temp", ".xml");
                try (FileWriter writer = new FileWriter(tempFile)) {
                    writer.write(GestionDocumentosHCE.obtenerDocumento(id));
                    writer.flush();
                } // de esta forma se guarda el archivo correctamente

                HCEAdapter adaptador = getOrCreateAdapter(tempFile);

                Paciente paciente = adaptador.getPaciente();
                String alturaValor = "";
                String alturaUnidad = "";
                String pesoValor = "";
                String pesoUnidad = "";

                List<Observacion> observaciones = adaptador.getObservaciones();
                //Busco la observacion que corresponda a la altura y peso
                //Codigo SNOMED CT --> 2.16.840.1.113883.6.96 en HL7
                for (Observacion obs : observaciones) {
                    if (Objects.equals(obs.getCodeSystem(), "2.16.840.1.113883.6.96") && Objects.equals(obs.getCode(), "50373000")) {
                        alturaValor = obs.getMeditionValue();
                        alturaUnidad = obs.getMeditionUnit();
                    } else {
                        if (Objects.equals(obs.getCodeSystem(), "2.16.840.1.113883.6.96") && Objects.equals(obs.getCode(), "363808001")) {
                            pesoValor = obs.getMeditionValue();
                            pesoUnidad = obs.getMeditionUnit();
                        }
                    }
                }

                //Si no está en observaciones busco en el texto libre
                String textoLibre = adaptador.getTextoLibre();
                if (alturaValor.isBlank() && alturaUnidad.isBlank()){
                    Pattern pattern = Pattern.compile("(\\d+(?:[.,]\\d+)?)\\s*(cm|m)\\b", Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(textoLibre);
                    if (matcher.find()) {
                        log.info(matcher.toString());
                        alturaValor = matcher.group(1);
                        alturaUnidad = matcher.group(2);
                    }
                }
                if (pesoValor.isBlank() && pesoUnidad.isBlank()){
                    Pattern patternPeso = Pattern.compile("(\\d+(?:[.,]\\d+)?)\\s*(kg|lb)\\b", Pattern.CASE_INSENSITIVE);
                    Matcher matcher = patternPeso.matcher(textoLibre);
                    if (matcher.find()) {
                        log.info(matcher.toString());
                        pesoValor = matcher.group(1);
                        pesoUnidad = matcher.group(2);
                    }
                }
                return new PacienteExtendido(paciente.getNombre().strip(), paciente.getGenero(), paciente.getFechaNacimiento(), paciente.getEstadoCivil(), paciente.getRaza(), paciente.getLugarNacimiento(), alturaValor, alturaUnidad, pesoValor, pesoUnidad);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            return null;
        }
    }

    /**
     * Realiza el procesamiento completo de una HCE, extrayendo los datos básicos del paciente,
     * en conjunto con los medicamentos que se encuentren en la HCE,
     * y su correspondiente mapeo de códigos UMLS/RxNorm y clasificación.
     * Es extendible a más información asociada al paciente.
     *
     * @param id identificador del documento cargado en memoria.
     * @return un objeto {@link DatosHCE} con la información médica consolidada o {@code null} si el documento no existe.
     */
    public DatosHCE obtenerDatosPacienteExtendido(String id){
        if (GestionDocumentosHCE.existeDocumento(id)) {
            try {
                PacienteExtendido datosPaciente = obtenerDatosPaciente(id);

                File tempFile = File.createTempFile("hce-temp", ".xml");
                try (FileWriter writer = new FileWriter(tempFile)) {
                    writer.write(GestionDocumentosHCE.obtenerDocumento(id));
                    writer.flush();
                } // de esta forma se guarda el archivo correctamente

                HCEAdapter adaptador = getOrCreateAdapter(tempFile);

                //ACA COMIENZA EL PROCESAMIENTO
                List<SustanciaAdministrada> noClasificados = new ArrayList<>();
                DatosHCE.Medicamentos.Clasificados clasificados = new DatosHCE.Medicamentos.Clasificados();
                clasificados.setDiureticos(new ArrayList<>());
                clasificados.setNoDiureticos(new ArrayList<>());

                //Medicamentos parte estructurada de la HCE
                List<SustanciaAdministrada> medicamentos = adaptador.getMedicamentos();

                String textoLibre = adaptador.getTextoLibre();
                //Lista con los medicamentos extraídos del texto libre
                //Por lo general con código RXNORM y CUI
                List<SustanciaAdministrada> medsTextoLibre = processPlainTextHCE(textoLibre);

                poblarCodigosRxNorm(medicamentos, umlsApiKey);
                poblarCodigosRxNorm(medsTextoLibre, umlsApiKey);

                for(SustanciaAdministrada sust: medsTextoLibre){
                    agregarSustanciaSiNoExiste(medicamentos, sust);
                }

                for (SustanciaAdministrada sust : medicamentos){
                    Boolean esDiuretico = esDiuretico(sust);
                    if (esDiuretico == null){
                        //Lo agrego a la lista de sin clasificar
                        noClasificados.add(sust);
                    } else if(esDiuretico){
                        //Lo agrego a la lista de diureticos
                        clasificados.getDiureticos().add(sust);
                    } else {
                        //Lo agrego a la lista de NO diureticos
                        clasificados.getNoDiureticos().add(sust);
                    }
                }

                DatosHCE datosHCE = new DatosHCE();
                datosHCE.setDatosBasicosPaciente(datosPaciente);
                DatosHCE.Medicamentos meds = new DatosHCE.Medicamentos();
                meds.setClasificados(clasificados);
                meds.setNoClasificados(noClasificados);
                datosHCE.setMedicamentos(meds);

                return datosHCE;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }else{
            return null;
        }
    }

    public Map<String, SustanciaAdministrada> obtenerMedicamentosTextoLibreHCE(File fileHCE) throws IOException {
        log.info("*** ObtenerMedicamentosTextoLibreHCE ***");
        log.info("Comienza la extracción de medicamentos en TEXTO LIBRE");
        Map<String, SustanciaAdministrada> meds = new HashMap<String, SustanciaAdministrada>();


        HCEAdapter adaptador = getOrCreateAdapter(fileHCE);

        String textoLibre = adaptador.getTextoLibre();
        //Lista con los medicamentos extraídos del texto libre
        //Por lo general con código RXNORM y CUI
        List<SustanciaAdministrada> medsTextoLibre = processPlainTextHCE(textoLibre);
        for(SustanciaAdministrada sust : medsTextoLibre){
            meds.put(sust.getName(), sust);
        }

        return meds;
    }

    /**
     * Procesa el texto libre de una HCE, sustituyendo nombres comerciales por principios activos,
     * traduciendo el texto y extrayendo las sustancias mencionadas con sus códigos UMLS.
     *
     * @param inputText texto libre extraído de la historia clínica.
     * @return lista de {@link SustanciaAdministrada} detectadas en el texto.
     */
    public List<SustanciaAdministrada> processPlainTextHCE (String inputText){
        log.info("*** ProcessPlainTextHCE ***");
        log.info("Comienza el procesamiento del texto: {}", inputText);
        //Sustitucion de nombres comerciales a principios activos
        Map<String, String> nombresAsustituir = languajeExpansion.obtenerPrincipiosActivos(inputText);
        String textoProcesado = inputText.toLowerCase();
        for (Map.Entry<String, String> nombre : nombresAsustituir.entrySet()) {
            String nombreEnTexto = nombre.getKey(); //nombre que tenía en el input text
            String principioActivo = nombre.getValue(); //principio activo obtenido
            textoProcesado = textoProcesado.replace(nombreEnTexto,principioActivo);
        }
        log.info("Se sustituyeron los nombres comerciales: {}", textoProcesado);

        //Traduccion del texto
        textoProcesado = translator.translate(textoProcesado, sourceLang, targetLang);
        log.info("El texto traducido queda: {}", textoProcesado);

        //Extraccion de medicamentos
        List<SustanciaAdministrada> cuisMedicamentos = plainTextProcessor.extractDrugs(textoProcesado);

        //Agrego los codigos RXNorm
        poblarCodigosRxNorm(cuisMedicamentos, this.umlsApiKey);

        log.info("Los medicamentos extraidos son: {}", cuisMedicamentos);
        return cuisMedicamentos;

    }


    /**
     * Realiza el procesamiento completo de una HCE, extrayendo los datos básicos del paciente,
     * en conjunto con los medicamentos que se encuentren en la HCE,
     * y su correspondiente mapeo de códigos UMLS/RxNorm y clasificación.
     * Luego a partir de esos datos, y datos recidifos de un formulario con formato
     * compatible con la ontología seleccionada y el modelo seleccionado:
     * Para MSP_UY:
     *     "http://purl.org/ontology/breast_cancer_recommendation#UY_age_question" --> edad
     *     "http://purl.org/ontology/breast_cancer_recommendation#UY_chest_radiotherapy_question"
     *     "http://purl.org/ontology/breast_cancer_recommendation#UY_hereditary_risk_question" --> si tiene riesgo hereditario
     *     "http://purl.org/ontology/breast_cancer_recommendation#UY_hiperplasia_atipia_question" --> si tiene hiperplasia
     * Infiere las recomendaciones utilizando la ontología.
     *
     * @param id identificador del documento cargado en memoria.
     * @param womanHistoryData recibe la información asociada a la ontología + modelo utilizado
     * @return un objeto {@link PacienteRecomendacion} con la información médica consolidada o null si el paciente no existe
     */
    public PacienteRecomendacion obtenerDatosPacienteExtendidoConRecomendaciones(String id, Map<String, String> womanHistoryData){
        DatosHCE datosPaciente = obtenerDatosPacienteExtendido(id);
        if (datosPaciente != null) {
            // Valores por defecto para el procesamiento
            RiskModel riskModel = RiskModel.MSP_UY; // Usar MSP_UY como modelo por defecto
            // Obtengo las recomendaciones a partir de la información del paciente
            ReasoningResult razonamiento = instanciateOntology.processWomanWithMedicationAndReasoning(
                    riskModel,
                    womanHistoryData,
                    datosPaciente);
            log.info("Información extraída correctamente e inferencias realizadas para paciente {}", id);
            return new PacienteRecomendacion(datosPaciente, razonamiento);
        } else {
            log.error("NO existe paciente con id {} en el sistema", id);
            return null;}
    }

    /**
     * Obtiene los datos básicos de todos los pacientes almacenados en el sistema.
     *
     * @return lista de objetos {@link PacienteExtendido} con los datos de todos los pacientes
     */
    public List<PacienteExtendido> obtenerTodosLosPacientesBasicos() {
        List<PacienteExtendido> pacientes = new ArrayList<>();
        
        for (String id : GestionDocumentosHCE.obtenerTodosLosIds()) {
            PacienteExtendido paciente = obtenerDatosPaciente(id);
            if (paciente != null) {
                pacientes.add(paciente);
            }
        }
        
        return pacientes;
    }
}


