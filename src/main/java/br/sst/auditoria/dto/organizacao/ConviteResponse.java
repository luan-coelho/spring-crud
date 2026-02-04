package br.sst.auditoria.dto.organizacao;

import br.sst.auditoria.model.Convite;

import java.time.LocalDateTime;

/**
 * DTO de resposta para convite
 */
public record ConviteResponse(
    String id,
    String email,
    String convidadorId,
    String convidadorNome,
    String organizacaoId,
    String organizacaoNome,
    String papel,
    String status,
    LocalDateTime criadoEm,
    LocalDateTime expiraEm
) {
    public static ConviteResponse fromEntity(Convite convite) {
        return new ConviteResponse(
            convite.getId(),
            convite.getEmail(),
            convite.getConvidador().getId(),
            convite.getConvidador().getNome(),
            convite.getOrganizacao().getId(),
            convite.getOrganizacao().getNome(),
            convite.getPapel(),
            convite.getStatus(),
            convite.getCriadoEm(),
            convite.getExpiraEm()
        );
    }
}
