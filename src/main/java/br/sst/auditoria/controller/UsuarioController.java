package br.sst.auditoria.controller;

import br.sst.auditoria.model.Usuario;
import br.sst.auditoria.service.UsuarioService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        return "usuarios/index";
    }

    @GetMapping("/novo")
    public String createForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios/form :: form";
    }

    @GetMapping("/{id}/editar")
    public String editForm(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.findById(id).orElseThrow();
        usuario.setSenha(""); // Don't expose password
        model.addAttribute("usuario", usuario);
        return "usuarios/form :: form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute Usuario usuario, BindingResult bindingResult,
            Model model, HttpServletResponse response) {
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            response.setStatus(422); // Unprocessable Entity
            return "usuarios/form :: form";
        }

        // Handle password update for existing users
        if (usuario.getId() != null) {
            Usuario existing = usuarioService.findById(usuario.getId()).orElse(null);
            if (existing != null && (usuario.getSenha() == null || usuario.getSenha().isEmpty())) {
                usuario.setSenha(existing.getSenha());
            }
        }

        // Validate password for new users
        if (usuario.getId() == null && (usuario.getSenha() == null || usuario.getSenha().isEmpty())) {
            bindingResult.rejectValue("senha", "error.usuario", "A senha é obrigatória para novos usuários");
            response.setStatus(422); // Unprocessable Entity
            return "usuarios/form :: form";
        }

        usuarioService.save(usuario);
        model.addAttribute("usuarios", usuarioService.findAll());
        return "usuarios/index :: lista";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id, Model model) {
        usuarioService.deleteById(id);
        model.addAttribute("usuarios", usuarioService.findAll());
        return "usuarios/index :: lista";
    }

}
