package br.sst.auditoria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "session", indexes = {
        @Index(name = "session_userId_idx", columnList = "user_id")
})
public class Sessao implements Serializable {

    @Id
    @Column(nullable = false)
    private String id;

    @NotNull(message = "A data de expiração é obrigatória")
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiraEm;

    @NotBlank(message = "O token é obrigatório")
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime atualizadoEm;

    @Column(name = "ip_address")
    private String enderecoIp;

    @Column(name = "user_agent")
    private String agenteUsuario;

    @Column(name = "active_organization_id")
    private String organizacaoAtivaId;

    @Column(name = "impersonated_by")
    private String personificadoPor;

    // Relacionamento com Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Usuario usuario;
}
