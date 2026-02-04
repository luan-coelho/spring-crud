package br.sst.auditoria.dto.organizacao;

import br.sst.auditoria.model.Membro;

import java.time.LocalDateTime;

/**
 * DTO de resposta para membro
 */
public record MembroResponse(
    String id,
    String usuarioId,
    String usuarioNome,
    String usuarioEmail,
    String organizacaoId,
    String papel,
    LocalDateTime criadoEm
) {
    public static MembroResponse fromEntity(Membro membro) {
        return new MembroResponse(
            membro.getId(),
            membro.getUsuario().getId(),
            membro.getUsuario().getNome(),
            membro.getUsuario().getEmail(),
            membro.getOrganizacao().getId(),
            membro.getPapel(),
            membro.getCriadoEm()
        );
    }
}
