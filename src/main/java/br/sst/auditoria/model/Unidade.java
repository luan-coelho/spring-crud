package br.sst.auditoria.model;

import br.sst.auditoria.model.enums.Situacao;
import br.sst.auditoria.model.enums.TipoDocumento;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Unidade
 * Representa um estabelecimento/filial da empresa
 * Necessário para eSocial (S-2240) e cálculo de multas NR 28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "unidade",
    indexes = {
        @Index(name = "idx_unidade_organizacao_id", columnList = "organizacao_id"),
        @Index(name = "idx_unidade_empresa_id", columnList = "empresa_id"),
        @Index(name = "idx_unidade_situacao", columnList = "situacao")
    }
)
public class Unidade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull(message = "Organização é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacao_id", nullable = false)
    private Organizacao organizacao;

    @NotNull(message = "Empresa é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false)
    @Builder.Default
    private Situacao situacao = Situacao.ATIVO;

    @NotBlank(message = "Nome da unidade é obrigatório")
    @Size(max = 100)
    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @NotNull(message = "Tipo de documento é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;

    @NotBlank(message = "Número do documento é obrigatório")
    @Size(max = 14)
    @Column(name = "numero_documento", length = 14, nullable = false)
    private String numeroDocumento;

    @NotBlank(message = "CNAE é obrigatório")
    @Size(max = 7)
    @Column(name = "cnae", length = 7, nullable = false)
    private String cnae;

    @NotNull(message = "Grau de risco é obrigatório")
    @Column(name = "grau_risco", nullable = false)
    private Short grauRisco;

    @NotNull
    @Column(name = "is_matriz", nullable = false)
    @Builder.Default
    private Boolean isMatriz = false;

    @NotNull(message = "Endereço é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endereco_id", nullable = false)
    private Endereco endereco;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    // Relacionamentos
    @OneToMany(mappedBy = "unidade", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Setor> setores = new ArrayList<>();

    @OneToMany(mappedBy = "unidade", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Funcionario> funcionarios = new ArrayList<>();
}
