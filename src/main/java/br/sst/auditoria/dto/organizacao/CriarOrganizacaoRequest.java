package br.sst.auditoria.dto.organizacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para criar uma nova organização
 */
public record CriarOrganizacaoRequest(
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    String nome,

    @NotBlank(message = "Slug é obrigatório")
    @Size(max = 50, message = "Slug deve ter no máximo 50 caracteres")
    String slug,

    @Size(max = 255, message = "Logo deve ter no máximo 255 caracteres")
    String logo,

    String metadados
) {}
