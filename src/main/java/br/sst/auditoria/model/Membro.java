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
@Table(name = "membro", indexes = {
        @Index(name = "membro_organizacao_id_idx", columnList = "organizacao_id"),
        @Index(name = "membro_usuario_id_idx", columnList = "usuario_id")
})
public class Membro implements Serializable {

    @Id
    @Column(nullable = false)
    private String id;

    @Builder.Default
    @NotBlank(message = "O papel é obrigatório")
    @Column(name = "papel", nullable = false)
    private String papel = "MEMBRO";

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    // Relacionamento com Organizacao
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacao_id", nullable = false)
    private Organizacao organizacao;

    // Relacionamento com Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
