package uy.com.fing.hicscan.hceanalysis.usecases;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uy.com.fing.hicscan.hceanalysis.adapters.HCEAdapter;
import uy.com.fing.hicscan.hceanalysis.adapters.HCEAdapterFactory;
import uy.com.fing.hicscan.hceanalysis.data.plainTextProcessor.PlainTextProcessor;
import uy.com.fing.hicscan.hceanalysis.data.translator.Translator;
import uy.com.fing.hicscan.hceanalysis.dto.*;
import uy.com.fing.hicscan.hceanalysis.languageexpansion.LanguageExpansion;
import uy.com.fing.hicscan.hceanalysis.utils.GestionDocumentosHCE;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

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
    public ProcessHceUseCase (PlainTextProcessor plainTextProcessor, Translator translator, LanguageExpansion languageExpansion, HCEAdapterFactory hceAdapter){
        this.plainTextProcessor = plainTextProcessor;
        this.translator = translator;
        this.languajeExpansion = languageExpansion;
        this.hceAdapterFactory = hceAdapter;
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
                //TO DO: REVISAR (esto se re puede mejorar ahora queda así)
                //Yo le doy la extensión es al pedo, voy a hacer otro endpoint

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

                //TO DO: Agregar que compare las listas y no genere repetidos
                medicamentos.addAll(medsTextoLibre);

                poblarCodigosRxNorm(medicamentos, umlsApiKey);

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

}


