package br.sst.auditoria.model;

import br.sst.auditoria.model.enums.Situacao;
import br.sst.auditoria.model.enums.TipoDocumento;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
 * Entidade Empresa
 * Representa a pessoa jurídica (vínculo legal)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "empresa",
    indexes = {
        @Index(name = "idx_empresa_organizacao_id", columnList = "organizacao_id"),
        @Index(name = "idx_empresa_situacao", columnList = "situacao")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_empresa_documento",
            columnNames = {"organizacao_id", "tipo_documento", "numero_documento"}
        )
    }
)
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull(message = "Organização é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacao_id", nullable = false)
    private Organizacao organizacao;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false)
    @Builder.Default
    private Situacao situacao = Situacao.ATIVO;

    @NotBlank(message = "Razão social é obrigatória")
    @Size(max = 200)
    @Column(name = "razao_social", length = 200, nullable = false)
    private String razaoSocial;

    @Size(max = 100)
    @Column(name = "nome_fantasia", length = 100)
    private String nomeFantasia;

    @NotNull(message = "Tipo de documento é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;

    @NotBlank(message = "Número do documento é obrigatório")
    @Size(max = 14)
    @Column(name = "numero_documento", length = 14, nullable = false)
    private String numeroDocumento;

    @Size(max = 20)
    @Column(name = "inscricao_estadual", length = 20)
    private String inscricaoEstadual;

    @Email(message = "E-mail inválido")
    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 15)
    @Column(name = "telefone", length = 15, nullable = false)
    private String telefone;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    // Relacionamentos
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Unidade> unidades = new ArrayList<>();

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Cargo> cargos = new ArrayList<>();

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Funcionario> funcionarios = new ArrayList<>();
}
