package br.sst.auditoria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("error", "E-mail ou senha inválidos");
        }

        if (logout != null) {
            model.addAttribute("message", "Logout realizado com sucesso");
        }

        return "views/auth/login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) boolean rememberMe,
            RedirectAttributes redirectAttributes) {

        // TODO: Implementar autenticação (Spring Security)
        // Por enquanto, apenas redirecionando para dashboard

        if (email.isEmpty() || password.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "E-mail e senha são obrigatórios");
            return "redirect:/auth/login?error";
        }

        // Simulação de login bem-sucedido
        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout() {
        // TODO: Implementar logout (Spring Security)
        return "redirect:/auth/login?logout";
    }
}
