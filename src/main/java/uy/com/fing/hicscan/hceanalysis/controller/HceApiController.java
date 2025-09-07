package uy.com.fing.hicscan.hceanalysis.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uy.com.fing.hicscan.hceanalysis.dto.ApiResponse;
import uy.com.fing.hicscan.hceanalysis.usecases.ProcessHceUseCase;

import java.util.Map;

@RestController
@RequestMapping("/HCE")
public class HceApiController {
    private final ProcessHceUseCase processHceUseCase;
    private HceApiController(ProcessHceUseCase processHceUseCase){
        this.processHceUseCase = processHceUseCase;
    }

    @GetMapping("/")
    public String index() {
        return "Servidor funcionando correctamente.";
    }

    @GetMapping("/processHCE")
    public ApiResponse processHCE(
            @RequestParam String inputText
    ){
        Map<String,String> meds = processHceUseCase.processPlainTextHCE(inputText);
        return new ApiResponse("success", "Datos extraídos correctamente.", meds);
    }

}
