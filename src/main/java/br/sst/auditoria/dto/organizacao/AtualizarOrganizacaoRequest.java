package br.sst.auditoria.dto.organizacao;

import jakarta.validation.constraints.Size;

/**
 * DTO para atualizar uma organização
 */
public record AtualizarOrganizacaoRequest(
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    String nome,

    @Size(max = 50, message = "Slug deve ter no máximo 50 caracteres")
    String slug,

    @Size(max = 255, message = "Logo deve ter no máximo 255 caracteres")
    String logo,

    String metadados
) {}
