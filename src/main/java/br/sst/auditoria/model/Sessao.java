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
 * Entidade Sessão (Better Auth)
 * Armazena sessões de usuário com organização ativa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sessao", indexes = {
        @Index(name = "idx_sessao_usuario_id", columnList = "usuario_id"),
        @Index(name = "idx_sessao_organizacao_ativa_id", columnList = "organizacao_ativa_id"),
        @Index(name = "idx_sessao_token", columnList = "token")
})
public class Sessao implements Serializable {

    @Id
    @Column(nullable = false)
    private String id;

    @NotNull(message = "A data de expiração é obrigatória")
    @Column(name = "expira_em", nullable = false)
    private LocalDateTime expiraEm;

    @NotBlank(message = "O token é obrigatório")
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @Column(name = "endereco_ip")
    private String enderecoIp;

    @Column(name = "agente_usuario")
    private String agenteUsuario;

    @Column(name = "personificado_por")
    private String personificadoPor;

    // Relacionamento com Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Relacionamento com Organizacao (organização ativa na sessão)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacao_ativa_id")
    private Organizacao organizacaoAtiva;
}
