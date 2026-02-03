package br.sst.auditoria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "views/dashboard";
    }

    @GetMapping("/")
    public String home() {
        // TODO: Quando implementar Spring Security, verificar autenticação aqui
        // Por enquanto redirecionando para login
        return "redirect:/auth/login";
    }

}
