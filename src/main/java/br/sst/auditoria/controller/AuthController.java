package br.sst.auditoria.controller;

import br.sst.auditoria.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "expired", required = false) String expired,
            Model model) {

        if (error != null) {
            model.addAttribute("error", "E-mail ou senha inválidos");
        }

        if (logout != null) {
            model.addAttribute("message", "Logout realizado com sucesso");
        }

        if (expired != null) {
            model.addAttribute("error", "Sua sessão expirou. Por favor, faça login novamente.");
        }

        return "views/auth/login";
    }

    // O POST /auth/login é tratado pelo Spring Security automaticamente

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/auth/login?logout=true";
    }

    @GetMapping("/cadastro")
    public String cadastro(Model model) {
        return "views/auth/cadastro";
    }

    @PostMapping("/cadastro")
    public String doCadastro(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String cpf,
            @RequestParam String senha,
            @RequestParam String confirmarSenha,
            @RequestParam(required = false) String telefone,
            RedirectAttributes redirectAttributes) {

        // Validações básicas
        if (!senha.equals(confirmarSenha)) {
            redirectAttributes.addFlashAttribute("error", "As senhas não coincidem");
            return "redirect:/auth/cadastro";
        }

        try {
            authService.registrar(nome, email, cpf, senha, telefone);
            redirectAttributes.addFlashAttribute("message",
                    "Cadastro realizado com sucesso! Faça login para continuar.");
            return "redirect:/auth/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/cadastro";
        }
    }

    @GetMapping("/esqueci-senha")
    public String esqueciSenha(Model model) {
        return "views/auth/esqueci-senha";
    }

    @PostMapping("/esqueci-senha")
    public String doEsqueciSenha(
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {

        // TODO: Implementar envio de e-mail para recuperação de senha
        redirectAttributes.addFlashAttribute("message",
                "Se o e-mail estiver cadastrado, você receberá instruções para redefinir sua senha.");
        return "redirect:/auth/login";
    }
}
