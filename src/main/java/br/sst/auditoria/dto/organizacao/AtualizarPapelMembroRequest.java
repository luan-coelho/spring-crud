package br.sst.auditoria.dto.organizacao;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para atualizar papel de um membro
 */
public record AtualizarPapelMembroRequest(
    @NotBlank(message = "Papel é obrigatório")
    String papel,

    @NotBlank(message = "ID do membro é obrigatório")
    String membroId,

    String organizacaoId
) {}
