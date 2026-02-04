package br.sst.auditoria.security.session;

import br.sst.auditoria.model.Sessao;
import br.sst.auditoria.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Utilitário para acessar informações da sessão atual no contexto de segurança.
 */
public final class SessionUtils {

    private SessionUtils() {
        // Classe utilitária
    }

    /**
     * Obtém a sessão atual do contexto de segurança
     */
    public static Optional<Sessao> getSessaoAtual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof SessionAuthenticationToken sessionToken) {
            return Optional.ofNullable(sessionToken.getSessao());
        }
        return Optional.empty();
    }

    /**
     * Obtém o token da sessão atual
     */
    public static Optional<String> getSessionToken() {
        return getSessaoAtual().map(Sessao::getToken);
    }

    /**
     * Obtém o ID da organização ativa na sessão atual
     */
    public static Optional<String> getOrganizacaoAtivaId() {
        return getSessaoAtual()
                .filter(s -> s.getOrganizacaoAtiva() != null)
                .map(s -> s.getOrganizacaoAtiva().getId());
    }

    /**
     * Obtém o ID do usuário atual
     */
    public static Optional<String> getUsuarioId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return Optional.of(userDetails.getId());
        }
        return Optional.empty();
    }

    /**
     * Verifica se há uma sessão válida no contexto
     */
    public static boolean hasValidSession() {
        return getSessaoAtual().isPresent();
    }

    /**
     * Obtém o CustomUserDetails atual
     */
    public static Optional<CustomUserDetails> getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return Optional.of(userDetails);
        }
        return Optional.empty();
    }
}
