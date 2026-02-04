package br.sst.auditoria.dto.organizacao;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para adicionar membro diretamente (sem convite)
 */
public record AdicionarMembroRequest(
    String usuarioId,

    @NotBlank(message = "Papel é obrigatório")
    String papel,

    String organizacaoId
) {}
