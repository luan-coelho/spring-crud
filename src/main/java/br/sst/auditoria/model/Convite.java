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
@Table(name = "convite", indexes = {
        @Index(name = "convite_organizacao_id_idx", columnList = "organizacao_id"),
        @Index(name = "convite_email_idx", columnList = "email")
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
    @Column(name = "papel", nullable = false)
    private String papel = "MEMBRO";

    @Builder.Default
    @NotBlank(message = "O status é obrigatório")
    @Column(name = "status", nullable = false)
    private String status = "PENDENTE";

    @NotNull(message = "A data de expiração é obrigatória")
    @Column(name = "expira_em", nullable = false)
    private LocalDateTime expiraEm;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    // Relacionamento com Organizacao
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacao_id", nullable = false)
    private Organizacao organizacao;

    // Relacionamento com Usuario (convidador)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "convidador_id", nullable = false)
    private Usuario convidador;
}
