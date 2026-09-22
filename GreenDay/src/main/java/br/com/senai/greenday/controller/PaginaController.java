package br.com.senai.greenday.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginaController {

    @GetMapping("/propriedades")
    public String propriedades() {
        return "propriedades";
    }

    @GetMapping("/culturas")
    public String culturas() {
        return "culturas";
    }

    @GetMapping("/sensores")
    public String sensores() {
        return "sensores";
    }

    @GetMapping("/clima")
    public String clima() {
        return "clima";
    }

    @GetMapping("/alertas")
    public String alertas() {
        return "alertas";
    }

    @GetMapping("/irrigacao")
    public String irrigacao() {
        return "irrigacao";
    }

    @GetMapping("/relatorios")
    public String relatorios() {
        return "relatorios";
    }
}
