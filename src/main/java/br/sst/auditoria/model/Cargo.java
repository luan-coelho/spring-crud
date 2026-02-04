package br.sst.auditoria.model;

import br.sst.auditoria.model.enums.Status;
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
 * Entidade Cargo
 * Representa uma função/cargo na empresa (Global/Empresa)
 * Vincula CBO e descrição de atividades para PGR/LTCAT
 * Padronização: Um cargo tem o mesmo CBO em qualquer unidade
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "cargo",
    indexes = {
        @Index(name = "idx_cargo_organizacao_id", columnList = "organizacao_id"),
        @Index(name = "idx_cargo_empresa_id", columnList = "empresa_id")
    }
)
public class Cargo {

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
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Status status = Status.ACTIVE;

    @NotBlank(message = "Nome do cargo é obrigatório")
    @Size(max = 100)
    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @NotBlank(message = "CBO é obrigatório")
    @Size(max = 6)
    @Column(name = "cbo", length = 6, nullable = false)
    private String cbo;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    // Relacionamentos
    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Funcionario> funcionarios = new ArrayList<>();
}
