package br.sst.auditoria.security.session;

import br.sst.auditoria.model.Conta;
import br.sst.auditoria.model.Sessao;
import br.sst.auditoria.model.Usuario;
import br.sst.auditoria.repository.ContaRepository;
import br.sst.auditoria.security.CustomUserDetails;
import br.sst.auditoria.service.SessaoService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Filtro de autenticação baseado em sessão persistida no banco de dados.
 * Substitui o JwtAuthenticationFilter para usar o modelo Better Auth.
 * 
 * Suporta token via:
 * - Header Authorization: Bearer <token>
 * - Cookie: session_token=<token>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SessionAuthenticationFilter extends OncePerRequestFilter {

    private final SessaoService sessaoService;
    private final ContaRepository contaRepository;

    public static final String SESSION_COOKIE_NAME = "session_token";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String sessionToken = extrairToken(request);

            if (sessionToken != null) {
                Optional<Sessao> sessaoOpt = sessaoService.validarSessao(sessionToken);

                if (sessaoOpt.isPresent()) {
                    Sessao sessao = sessaoOpt.get();
                    Usuario usuario = sessao.getUsuario();

                    // Busca a senha da conta de credenciais para o CustomUserDetails
                    String senha = contaRepository
                            .findByUsuarioIdAndProvedorId(usuario.getId(), "credentials")
                            .map(Conta::getSenha)
                            .orElse(null);

                    CustomUserDetails userDetails = new CustomUserDetails(usuario, senha);

                    // Define o contexto de autenticação com informações da sessão
                    UsernamePasswordAuthenticationToken authentication = new SessionAuthenticationToken(
                            userDetails,
                            sessao,
                            userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("Autenticação via sessão bem-sucedida para usuário: {}", usuario.getEmail());
                }
            }
        } catch (Exception e) {
            log.error("Não foi possível definir autenticação do usuário: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrai o token de sessão do request.
     * Prioridade: Header Authorization > Cookie
     */
    private String extrairToken(HttpServletRequest request) {
        // Tenta extrair do header Authorization
        String headerAuth = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(BEARER_PREFIX)) {
            return headerAuth.substring(BEARER_PREFIX.length());
        }

        // Tenta extrair do cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (SESSION_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
