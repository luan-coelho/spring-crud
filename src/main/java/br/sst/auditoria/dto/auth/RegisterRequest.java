package br.sst.auditoria.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "O nome é obrigatório")
    String nome,
    
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    String email,
    
    @NotBlank(message = "O CPF é obrigatório")
    String cpf,
    
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    String senha,
    
    @NotBlank(message = "A confirmação de senha é obrigatória")
    String confirmarSenha,
    
    String telefone
) {}
