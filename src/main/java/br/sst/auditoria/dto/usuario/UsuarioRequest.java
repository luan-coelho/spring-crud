package br.sst.auditoria.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioRequest(
    @NotBlank(message = "O nome é obrigatório")
    String nome,
    
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    String email,
    
    @NotBlank(message = "O CPF é obrigatório")
    String cpf,
    
    String telefone,
    String imagem,
    String papel
) {}
