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

/**
 * Papel de Organização (Organization Role)
 * Define papéis customizados com permissões específicas para uma organização
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "papel_organizacao",
    indexes = {
        @Index(name = "idx_papel_organizacao_organizacao_id", columnList = "organizacao_id"),
        @Index(name = "idx_papel_organizacao_papel", columnList = "papel")
    }
)
public class PapelOrganizacao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull(message = "Organização é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacao_id", nullable = false)
    private Organizacao organizacao;

    @NotBlank(message = "O papel é obrigatório")
    @Column(name = "papel", nullable = false)
    private String papel;

    @NotBlank(message = "A permissão é obrigatória")
    @Column(name = "permissao", nullable = false, columnDefinition = "TEXT")
    private String permissao;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;
}
