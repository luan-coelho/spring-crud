package br.sst.auditoria.controller;

import br.sst.auditoria.model.Usuario;
import br.sst.auditoria.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Lista todos os usuários
    @GetMapping
    public String list(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        return "views/usuarios/lista";
    }

    // Exibe formulário de criação
    @GetMapping("/novo")
    public String createForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "views/usuarios/form";
    }

    // Exibe um usuário específico
    @GetMapping("/{id}")
    public String show(@PathVariable String id, Model model) {
        Usuario usuario = usuarioService.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        model.addAttribute("usuario", usuario);
        return "views/usuarios/visualizar";
    }

    // Exibe formulário de edição
    @GetMapping("/{id}/editar")
    public String editForm(@PathVariable String id, Model model) {
        Usuario usuario = usuarioService.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        model.addAttribute("usuario", usuario);
        return "views/usuarios/form";
    }

    // Cria um novo usuário
    @PostMapping
    public String create(@Valid @ModelAttribute("usuario") Usuario usuario, BindingResult bindingResult,
            Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("usuario", usuario);
            return "views/usuarios/form";
        }

        try {
            usuarioService.save(usuario);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Usuário criado com sucesso!");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException e) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("errorMessage", e.getMessage());
            return "views/usuarios/form";
        }
    }

    // Atualiza um usuário existente
    @PutMapping("/{id}")
    public String update(@PathVariable String id, @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("usuario", usuario);
            return "views/usuarios/form";
        }

        try {
            usuario.setId(id);
            usuarioService.save(usuario);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Usuário atualizado com sucesso!");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException e) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("errorMessage", e.getMessage());
            return "views/usuarios/form";
        }
    }

    // Exclui um usuário
    @DeleteMapping("/{id}")
    public String delete(@PathVariable String id, RedirectAttributes redirectAttributes) {
        usuarioService.deleteById(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Usuário excluído com sucesso!");
        return "redirect:/usuarios";
    }

}
