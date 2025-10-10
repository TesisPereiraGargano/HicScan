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

import java.io.File;
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

    public PacienteExtendido obtenerDatosPaciente(File fileHCE){
        HCEAdapter adaptador = getOrCreateAdapter(fileHCE);
        Paciente paciente = adaptador.getPaciente();
        String alturaValor = "";
        String alturaUnidad = "";
        String pesoValor = "";
        String pesoUnidad = "";

        List<Observacion> observaciones = adaptador.getObservaciones();
        //Busco la observacion que corresponda a la altura y peso
        //Codigo SNOMED CT --> 2.16.840.1.113883.6.96 en HL7
        for (Observacion obs : observaciones){
            if (Objects.equals(obs.getCodeSystem(), "2.16.840.1.113883.6.96") && Objects.equals(obs.getCode(), "50373000")){
                alturaValor = obs.getMeditionValue();
                alturaUnidad = obs.getMeditionUnit();
            } else {
                if (Objects.equals(obs.getCodeSystem(), "2.16.840.1.113883.6.96") && Objects.equals(obs.getCode(), "363808001")){
                    pesoValor = obs.getMeditionValue();
                    pesoUnidad = obs.getMeditionUnit();
                }
            }
        }

        return new PacienteExtendido(paciente.getNombre(), paciente.getGenero(), paciente.getFechaNacimiento(), paciente.getEstadoCivil(), paciente.getRaza(), paciente.getLugarNacimiento(), alturaValor, alturaUnidad, pesoValor, pesoUnidad);
    }

    public Map<String, SustanciaAdministrada> obtenerMedicamentosEstructuradosHCE(File fileHCE) throws IOException {

        log.info("*** ObtenerMedicamentosHCE ***");
        log.info("Comienza la extracción de medicamentos en ESTRUCTURA XML");
        Map<String, SustanciaAdministrada> meds = new HashMap<String, SustanciaAdministrada>();


        HCEAdapter adaptador = getOrCreateAdapter(fileHCE);

        String textoLibre = adaptador.getTextoLibre();
        //Lista con los medicamentos que vinieron en formato estructurado y sus códigos
        List<SustanciaAdministrada> medicamentos = adaptador.getMedicamentos();

        for(SustanciaAdministrada sust : medicamentos){
            meds.put(sust.getName(), sust);
        }

        /**
        //Lista con los medicamentos extraídos del texto libre
        //Por lo general con código RXNORM y CUI
        List<SustanciaAdministrada> medsTextoLibre = processPlainTextHCE(textoLibre);

        // Concatenar medsTextoLibre en medicamentos
        medicamentos.addAll(medsTextoLibre); //Lista que contiene todos los medicamentos relevados

        for (SustanciaAdministrada sust : medicamentos) {
            //Completo los CUIS (si corresponde) y hallo los RXNORM
            for (CodDiccionario droga : sust.getDrugsCodes()) {
                if (droga.getRxnorm().isBlank()){
                    if (droga.getCui().isBlank() && droga.getSnomedCT().isBlank()) {
                        log.error("[ProcessHCEUseCase] Error - el medicamento no puede tener todos los códigos vacíos");
                    } else if (droga.getCui().isBlank()) {
                        droga.setCui(obtenerCUIDesdeSNOMEDCT(droga.getSnomedCT(), umlsApiKey));
                    }
                    if (!droga.getCui().isBlank()){
                        droga.setRxnorm(obtenerRxNormDesdeCUI(droga.getCui(), this.umlsApiKey));
                    }
                }

            }

        }


        log.info("[ProcessUseCase] Medicamentos extraídos de la seccion medicamentos de la HCE");
        for (SustanciaAdministrada med : medicamentos) {
            String codigoSnomed = med.getDrugsCodes().get(0).getSnomedCT();
            log.info("[ProcessUseCase] Se agregó {}", codigoSnomed);
            medicamentosUnificados.putIfAbsent(codigoSnomed, med);
        }

        log.info("[ProcessUseCase] Medicamentos extraídos del texto plano, hay {} medicamentos", medsTextoLibre.size());
        // Agrego los medicamentos del texto libre solo si ya no están
        for (int i = 0; i < medsTextoLibre.size(); i++) {

            List<CodDiccionario> codDiccionarios = medsTextoLibre.get(i).getDrugsCodes();
            for (int j = 0; j < codDiccionarios.size(); j++){

                codDiccionarios.get(j).getRxnorm()
            }
            log.info("[ProcessUseCase] Codigo snomed {}", codigoSnomed);
            if (codigoSnomed != null && !medicamentosUnificados.containsKey(codigoSnomed)) {
                List<Map.Entry<String,String>> lista = new ArrayList<>();
                lista.add(new AbstractMap.SimpleEntry<>(codigoSnomed, "2.16.840.1.113883.5.112"));
                SustanciaAdministrada nuevoMed = new SustanciaAdministrada("", "", "", "", new ArrayList<>());
                medicamentosUnificados.put(codigoSnomed, nuevoMed);
            }
        }
         return medicamentosUnificados;
**/
       return meds;
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


