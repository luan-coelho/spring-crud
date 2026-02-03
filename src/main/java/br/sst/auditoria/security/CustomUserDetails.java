package br.sst.auditoria.security;

import br.sst.auditoria.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Usuario usuario;
    private final String senha;

    public CustomUserDetails(Usuario usuario, String senha) {
        this.usuario = usuario;
        this.senha = senha;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Converte o papel do usuário em authorities do Spring Security
        String papel = usuario.getPapel();
        if (papel == null || papel.isEmpty()) {
            papel = "user";
        }

        // Adiciona o prefixo ROLE_ se não existir
        String role = papel.toUpperCase();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !Boolean.TRUE.equals(usuario.getBanido());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(usuario.getEmailVerificado()) && !Boolean.TRUE.equals(usuario.getBanido());
    }

    // Métodos auxiliares para acessar dados do usuário
    public Usuario getUsuario() {
        return usuario;
    }

    public String getId() {
        return usuario.getId();
    }

    public String getNome() {
        return usuario.getNome();
    }

    public String getEmail() {
        return usuario.getEmail();
    }

    public String getImagem() {
        return usuario.getImagem();
    }

    public String getPapel() {
        return usuario.getPapel();
    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(usuario.getPapel());
    }
}
