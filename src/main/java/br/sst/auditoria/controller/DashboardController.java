package br.sst.auditoria.controller;

import br.sst.auditoria.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails usuarioLogado, Model model) {
        if (usuarioLogado != null) {
            model.addAttribute("usuarioNome", usuarioLogado.getNome());
            model.addAttribute("usuarioEmail", usuarioLogado.getEmail());
            model.addAttribute("usuarioImagem", usuarioLogado.getImagem());
            model.addAttribute("isAdmin", usuarioLogado.isAdmin());
        }
        return "views/dashboard";
    }

    @GetMapping("/")
    public String home() {
        // Redireciona para o dashboard se autenticado (Spring Security cuida disso)
        return "redirect:/dashboard";
    }

}
