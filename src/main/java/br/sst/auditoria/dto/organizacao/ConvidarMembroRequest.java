package br.sst.auditoria.dto.organizacao;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para convidar um membro para a organização
 */
public record ConvidarMembroRequest(
    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    String email,

    @NotBlank(message = "Papel é obrigatório")
    String papel,

    String organizacaoId,

    Boolean reenviar
) {}
