package br.sst.auditoria.model;

import br.sst.auditoria.model.enums.Situacao;
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
 * Entidade Endereço
 * Armazena endereços completos para unidades
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "endereco",
    indexes = {
        @Index(name = "idx_endereco_organizacao_id", columnList = "organizacao_id"),
        @Index(name = "idx_endereco_situacao", columnList = "situacao")
    }
)
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull(message = "Organização é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacao_id", nullable = false)
    private Organizacao organizacao;

    @Size(max = 60)
    @Column(name = "rotulo", length = 60)
    private String rotulo;

    @Size(max = 8)
    @Column(name = "cep", length = 8)
    private String cep;

    @NotBlank(message = "Logradouro é obrigatório")
    @Size(max = 180)
    @Column(name = "logradouro", length = 180, nullable = false)
    private String logradouro;

    @Size(max = 20)
    @Column(name = "numero", length = 20)
    private String numero;

    @Size(max = 80)
    @Column(name = "complemento", length = 80)
    private String complemento;

    @Size(max = 80)
    @Column(name = "bairro", length = 80)
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 80)
    @Column(name = "cidade", length = 80, nullable = false)
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Size(max = 2)
    @Column(name = "estado", length = 2, nullable = false)
    private String estado;

    @Size(max = 10)
    @Column(name = "codigo_ibge_cidade", length = 10)
    private String codigoIbgeCidade;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false)
    @Builder.Default
    private Situacao situacao = Situacao.ATIVO;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    // Relacionamentos
    @OneToMany(mappedBy = "endereco", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Unidade> unidades = new ArrayList<>();
}
