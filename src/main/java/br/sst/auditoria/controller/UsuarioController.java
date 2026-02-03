package br.sst.auditoria.controller;

import br.sst.auditoria.model.Usuario;
import br.sst.auditoria.service.UsuarioService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Lista todos os usuários
    @GetMapping
    public String list(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        return "usuarios/lista";
    }

    // Exibe formulário de criação
    @GetMapping("/novo")
    public String createForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios/form";
    }

    // Exibe um usuário específico
    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        model.addAttribute("usuario", usuario);
        return "usuarios/visualizar";
    }

    // Exibe formulário de edição
    @GetMapping("/{id}/editar")
    public String editForm(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.setSenha(""); // Don't expose password
        model.addAttribute("usuario", usuario);
        return "usuarios/form";
    }

    // Cria um novo usuário
    @PostMapping
    public String create(@Valid @ModelAttribute Usuario usuario, BindingResult bindingResult,
            Model model, RedirectAttributes redirectAttributes) {
        // Validate password for new users
        if (usuario.getSenha() == null || usuario.getSenha().isEmpty()) {
            bindingResult.rejectValue("senha", "error.usuario", "A senha é obrigatória");
        }

        if (bindingResult.hasErrors()) {
            return "usuarios/form";
        }

        usuarioService.save(usuario);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Usuário criado com sucesso!");
        return "redirect:/usuarios";
    }

    // Atualiza um usuário existente
    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Usuario usuario,
            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "usuarios/form";
        }

        // Handle password update
        Usuario existing = usuarioService.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (usuario.getSenha() == null || usuario.getSenha().isEmpty()) {
            usuario.setSenha(existing.getSenha());
        }

        usuario.setId(id);
        usuarioService.save(usuario);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Usuário atualizado com sucesso!");
        return "redirect:/usuarios";
    }

    // Exclui um usuário
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        usuarioService.deleteById(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Usuário excluído com sucesso!");
        return "redirect:/usuarios";
    }

}
