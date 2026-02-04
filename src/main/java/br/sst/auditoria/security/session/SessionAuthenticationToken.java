package br.sst.auditoria.security.session;

import br.sst.auditoria.model.Sessao;
import br.sst.auditoria.security.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Token de autenticação estendido que inclui informações da sessão persistida.
 * Permite acessar dados da sessão (como organização ativa) em qualquer lugar do contexto de segurança.
 */
public class SessionAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final Sessao sessao;

    public SessionAuthenticationToken(CustomUserDetails principal, Sessao sessao,
            Collection<? extends GrantedAuthority> authorities) {
        super(principal, null, authorities);
        this.sessao = sessao;
    }

    /**
     * Retorna a sessão associada a esta autenticação
     */
    public Sessao getSessao() {
        return sessao;
    }

    /**
     * Retorna o ID da organização ativa na sessão, se houver
     */
    public String getOrganizacaoAtivaId() {
        if (sessao != null && sessao.getOrganizacaoAtiva() != null) {
            return sessao.getOrganizacaoAtiva().getId();
        }
        return null;
    }

    /**
     * Retorna o ID da sessão
     */
    public String getSessaoId() {
        return sessao != null ? sessao.getId() : null;
    }

    /**
     * Retorna o token da sessão
     */
    public String getSessionToken() {
        return sessao != null ? sessao.getToken() : null;
    }
}
