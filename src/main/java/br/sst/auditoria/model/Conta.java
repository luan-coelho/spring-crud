package br.sst.auditoria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "conta", indexes = {
        @Index(name = "conta_usuario_id_idx", columnList = "usuario_id")
})
public class Conta implements Serializable {

    @Id
    @Column(nullable = false)
    private String id;

    @NotBlank(message = "O ID da conta é obrigatório")
    @Column(name = "conta_id", nullable = false)
    private String contaId;

    @NotBlank(message = "O ID do provedor é obrigatório")
    @Column(name = "provedor_id", nullable = false)
    private String provedorId;

    @Column(name = "token_acesso")
    private String tokenAcesso;

    @Column(name = "token_atualizacao")
    private String tokenAtualizacao;

    @Column(name = "token_id")
    private String tokenId;

    @Column(name = "token_acesso_expira_em")
    private LocalDateTime tokenAcessoExpiraEm;

    @Column(name = "token_atualizacao_expira_em")
    private LocalDateTime tokenAtualizacaoExpiraEm;

    @Column(name = "escopo")
    private String escopo;

    @Column(name = "senha")
    private String senha;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    // Relacionamento com Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
