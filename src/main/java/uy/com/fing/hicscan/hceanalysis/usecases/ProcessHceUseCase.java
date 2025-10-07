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

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class ProcessHceUseCase {
    private final PlainTextProcessor plainTextProcessor;
    private final Translator translator;
    private final LanguageExpansion languajeExpansion;
    private final HCEAdapterFactory hceAdapterFactory;
    private final Map<File, HCEAdapter> adapterCache = new HashMap<>();


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

    public Map<String, String> obtenerMedicamentosHCE(File fileHCE) throws IOException {
        log.info("*** ObtenerMedicamentosHCE ***");
        log.info("Comienza la extracción de medicamentos");
        Map<String, String> meds = new HashMap<String, String>();

        HCEAdapter adaptador = getOrCreateAdapter(fileHCE);

        String textoLibre = adaptador.getTextoLibre();
        //tengo el código en snomed ct
        List<SustanciaAdministrada> medicamentos = adaptador.getMedicamentos();

        //también retorna el código en SNOMED CT
        Map<String, String> medsTextoLibre = processPlainTextHCE(textoLibre);

        // Los voy a juntar en un hash de medicamentos usando como clave el código de SNOMED CT
        // Y que sean instancias de la clase SustanciaAdm
        Map<String, SustanciaAdministrada> medicamentosUnificados = new HashMap<>();

        for (SustanciaAdministrada med : medicamentos) {
            String codigoSnomed = med.getDrugsCodes().get(0).getKey();
            medicamentosUnificados.putIfAbsent(codigoSnomed, med);
        }

        // Agrego los medicamentos del texto libre solo si ya no están
        for (Map.Entry<String, String> entry : medsTextoLibre.entrySet()) {
            String codigoSnomed = entry.getKey();
            if (!medicamentosUnificados.containsKey(codigoSnomed)) {
                List<Map.Entry<String,String>> lista = new ArrayList<>();
                lista.add(new AbstractMap.SimpleEntry<>(codigoSnomed, "2.16.840.1.113883.5.112"));
                SustanciaAdministrada nuevoMed = new SustanciaAdministrada("", "", "", "", new ArrayList<>());
                medicamentosUnificados.put(codigoSnomed, nuevoMed);
            }
        }

        return meds;
    }
    public Map<String, String> processPlainTextHCE (String inputText){
        log.info("*** ProcessPlainTextHCE ***");
        log.info("Comienza el procesamiento del texto: {}", inputText);
        //Sustitucion de nombres comerciales a principios activos
        Map<String, String> nombresAsustituir = languajeExpansion.obtenerPrincipiosActivos(inputText);
        String textoProcesado = inputText;
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
        Map<String, String> cuisMedicamentos = plainTextProcessor.extractDrugs(textoProcesado);
        log.info("Los medicamentos extraidos son: {}", cuisMedicamentos);
        return cuisMedicamentos;

    }

}


