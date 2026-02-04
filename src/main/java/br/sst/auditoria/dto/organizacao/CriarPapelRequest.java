package br.sst.auditoria.dto.organizacao;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para criar papel de organização
 */
public record CriarPapelRequest(
    @NotBlank(message = "Nome do papel é obrigatório")
    String papel,

    @NotBlank(message = "Permissão é obrigatória")
    String permissao,

    String organizacaoId
) {}
