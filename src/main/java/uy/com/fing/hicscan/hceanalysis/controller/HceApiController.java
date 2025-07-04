package uy.com.fing.hicscan.hceanalysis.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HceApiController {
    @GetMapping("/")
    public String index() {
        return "Servidor funcionando correctamente.";
    }
}
