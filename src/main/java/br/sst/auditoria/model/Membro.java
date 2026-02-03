package br.sst.auditoria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "member", indexes = {
        @Index(name = "member_organizationId_idx", columnList = "organization_id"),
        @Index(name = "member_userId_idx", columnList = "user_id")
})
public class Membro implements Serializable {

    @Id
    @Column(nullable = false)
    private String id;

    @Builder.Default
    @NotBlank(message = "O papel é obrigatório")
    @Column(name = "role", nullable = false)
    private String papel = "member";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    // Relacionamento com Organizacao
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organizacao organizacao;

    // Relacionamento com Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Usuario usuario;
}
