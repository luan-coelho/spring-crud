package br.sst.auditoria.dto.auth;

import br.sst.auditoria.model.Sessao;

import java.time.LocalDateTime;

/**
 * DTO para resposta de sessão do usuário.
 * Usado para listar sessões ativas.
 */
public record SessaoResponse(
    String id,
    LocalDateTime criadoEm,
    LocalDateTime expiraEm,
    String enderecoIp,
    String agenteUsuario,
    String organizacaoAtivaId,
    String organizacaoAtivaNome,
    boolean sessaoAtual
) {
    /**
     * Cria um SessaoResponse a partir de uma entidade Sessao
     */
    public static SessaoResponse fromSessao(Sessao sessao) {
        return fromSessao(sessao, null);
    }

    /**
     * Cria um SessaoResponse a partir de uma entidade Sessao, marcando se é a sessão atual
     */
    public static SessaoResponse fromSessao(Sessao sessao, String tokenAtual) {
        String orgId = null;
        String orgNome = null;
        
        if (sessao.getOrganizacaoAtiva() != null) {
            orgId = sessao.getOrganizacaoAtiva().getId();
            orgNome = sessao.getOrganizacaoAtiva().getNome();
        }

        boolean isAtual = tokenAtual != null && tokenAtual.equals(sessao.getToken());

        return new SessaoResponse(
            sessao.getId(),
            sessao.getCriadoEm(),
            sessao.getExpiraEm(),
            mascaraIp(sessao.getEnderecoIp()),
            sessao.getAgenteUsuario(),
            orgId,
            orgNome,
            isAtual
        );
    }

    /**
     * Mascara parcialmente o endereço IP por privacidade
     * Ex: 192.168.1.100 -> 192.168.***
     */
    private static String mascaraIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return null;
        }
        
        // IPv4
        if (ip.contains(".")) {
            String[] parts = ip.split("\\.");
            if (parts.length == 4) {
                return parts[0] + "." + parts[1] + ".***";
            }
        }
        
        // IPv6 - mascara parte do endereço
        if (ip.contains(":")) {
            int colonCount = 0;
            StringBuilder masked = new StringBuilder();
            for (char c : ip.toCharArray()) {
                if (c == ':') {
                    colonCount++;
                }
                if (colonCount < 3) {
                    masked.append(c);
                } else {
                    break;
                }
            }
            return masked + ":***";
        }
        
        return ip;
    }
}
