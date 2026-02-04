package br.sst.auditoria.model;

import br.sst.auditoria.model.enums.SituacaoFuncionario;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade Funcionário
 * Ponto central onde todas as entidades se encontram
 * 
 * Hierarquia: Empresa -> Unidade -> Setor -> Funcionário <- Cargo
 * 
 * - empresa: Vínculo legal (quem assina carteira) e relatórios globais
 * - unidade: Necessário para eSocial (S-2240) e cálculo de multas NR 28
 * - setor: Localiza no "mapa de riscos" durante auditorias de campo
 * - cargo: Vincula CBO e descrição de atividades para PGR/LTCAT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "funcionario",
    indexes = {
        @Index(name = "idx_funcionario_organizacao_id", columnList = "organizacao_id"),
        @Index(name = "idx_funcionario_empresa_id", columnList = "empresa_id"),
        @Index(name = "idx_funcionario_unidade_id", columnList = "unidade_id"),
        @Index(name = "idx_funcionario_setor_id", columnList = "setor_id"),
        @Index(name = "idx_funcionario_cargo_id", columnList = "cargo_id"),
        @Index(name = "idx_funcionario_situacao", columnList = "situacao")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_funcionario_cpf",
            columnNames = {"empresa_id", "cpf"}
        )
    }
)
public class Funcionario {

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

    @NotNull(message = "Unidade é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id", nullable = false)
    private Unidade unidade;

    @NotNull(message = "Setor é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id", nullable = false)
    private Setor setor;

    @NotNull(message = "Cargo é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_id", nullable = false)
    private Cargo cargo;

    @NotNull(message = "Status é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false)
    private SituacaoFuncionario situacao;

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(max = 200)
    @Column(name = "nome_completo", length = 200, nullable = false)
    private String nomeCompleto;

    @NotBlank(message = "CPF é obrigatório")
    @Size(max = 11, min = 11)
    @Column(name = "cpf", length = 11, nullable = false)
    private String cpf;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "data_admissao")
    private LocalDate dataAdmissao;

    @Column(name = "data_demissao")
    private LocalDate dataDemissao;

    @Size(max = 30)
    @Column(name = "matricula", length = 30)
    private String matricula;

    @Email(message = "E-mail inválido")
    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 15)
    @Column(name = "telefone", length = 15)
    private String telefone;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;
}
