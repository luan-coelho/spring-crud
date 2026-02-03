package br.sst.auditoria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invitation", indexes = {
        @Index(name = "invitation_organizationId_idx", columnList = "organization_id"),
        @Index(name = "invitation_email_idx", columnList = "email")
})
public class Convite implements Serializable {

    @Id
    @Column(nullable = false)
    private String id;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Column(name = "email", nullable = false)
    private String email;

    @Builder.Default
    @NotBlank(message = "O papel é obrigatório")
    @Column(name = "role", nullable = false)
    private String papel = "member";

    @Builder.Default
    @NotBlank(message = "O status é obrigatório")
    @Column(name = "status", nullable = false)
    private String status = "pending";

    @NotNull(message = "A data de expiração é obrigatória")
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiraEm;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    // Relacionamento com Organizacao
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organizacao organizacao;

    // Relacionamento com Usuario (convidador)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", nullable = false)
    private Usuario convidador;
}
