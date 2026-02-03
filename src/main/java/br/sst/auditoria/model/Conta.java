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
@Table(name = "account", indexes = {
        @Index(name = "account_userId_idx", columnList = "user_id")
})
public class Conta implements Serializable {

    @Id
    @Column(nullable = false)
    private String id;

    @NotBlank(message = "O ID da conta é obrigatório")
    @Column(name = "account_id", nullable = false)
    private String contaId;

    @NotBlank(message = "O ID do provedor é obrigatório")
    @Column(name = "provider_id", nullable = false)
    private String provedorId;

    @Column(name = "access_token")
    private String tokenAcesso;

    @Column(name = "refresh_token")
    private String tokenAtualizacao;

    @Column(name = "id_token")
    private String tokenId;

    @Column(name = "access_token_expires_at")
    private LocalDateTime tokenAcessoExpiraEm;

    @Column(name = "refresh_token_expires_at")
    private LocalDateTime tokenAtualizacaoExpiraEm;

    @Column(name = "scope")
    private String escopo;

    @Column(name = "password")
    private String senha;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime atualizadoEm;

    // Relacionamento com Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Usuario usuario;
}
