package br.sst.auditoria.dto.organizacao;

import br.sst.auditoria.model.Organizacao;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de resposta para organização completa com membros e convites
 */
public record OrganizacaoCompletaResponse(
    String id,
    String nome,
    String slug,
    String logo,
    String metadados,
    LocalDateTime criadoEm,
    List<MembroResponse> membros,
    List<ConviteResponse> convites
) {
    public static OrganizacaoCompletaResponse fromEntity(
            Organizacao organizacao,
            List<MembroResponse> membros,
            List<ConviteResponse> convites
    ) {
        return new OrganizacaoCompletaResponse(
            organizacao.getId(),
            organizacao.getNome(),
            organizacao.getSlug(),
            organizacao.getLogo(),
            organizacao.getMetadados(),
            organizacao.getCriadoEm(),
            membros,
            convites
        );
    }
}
