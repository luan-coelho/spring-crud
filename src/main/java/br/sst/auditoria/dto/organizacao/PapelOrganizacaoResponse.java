package br.sst.auditoria.dto.organizacao;

import br.sst.auditoria.model.PapelOrganizacao;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO de resposta para papel de organização
 */
public record PapelOrganizacaoResponse(
    String id,
    String organizacaoId,
    String papel,
    String permissao,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {
    public static PapelOrganizacaoResponse fromEntity(PapelOrganizacao papel) {
        return new PapelOrganizacaoResponse(
            papel.getId(),
            papel.getOrganizacao().getId(),
            papel.getPapel(),
            papel.getPermissao(),
            papel.getCriadoEm(),
            papel.getAtualizadoEm()
        );
    }
}
