package br.sst.auditoria.dto.organizacao;

import br.sst.auditoria.model.Organizacao;

import java.time.LocalDateTime;

/**
 * DTO de resposta para organização
 */
public record OrganizacaoResponse(
    String id,
    String nome,
    String slug,
    String logo,
    String metadados,
    LocalDateTime criadoEm
) {
    public static OrganizacaoResponse fromEntity(Organizacao organizacao) {
        return new OrganizacaoResponse(
            organizacao.getId(),
            organizacao.getNome(),
            organizacao.getSlug(),
            organizacao.getLogo(),
            organizacao.getMetadados(),
            organizacao.getCriadoEm()
        );
    }
}
