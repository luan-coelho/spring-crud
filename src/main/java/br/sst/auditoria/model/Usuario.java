package br.sst.auditoria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    @Id
    @Column(nullable = false)
    private String id;

    @NotBlank(message = "O nome é obrigatório")
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "O CPF é obrigatório")
    @Column(name = "cpf", nullable = false, unique = true)
    private String cpf;

    @Column(name = "telefone")
    private String telefone;

    @Builder.Default
    @Column(name = "email_verificado", nullable = false)
    private Boolean emailVerificado = false;

    @Column(name = "imagem")
    private String imagem;

    @Builder.Default
    @Column(name = "onboarding_completo")
    private Boolean onboardingCompleto = false;

    @Builder.Default
    @Column(name = "papel")
    private String papel = "user";

    @Builder.Default
    @Column(name = "banido")
    private Boolean banido = false;

    @Column(name = "motivo_banimento")
    private String motivoBanimento;

    @Column(name = "banimento_expira_em")
    private LocalDateTime banimentoExpiraEm;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    // Relacionamentos
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Sessao> sessoes = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Conta> contas = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Membro> membros = new ArrayList<>();

    @OneToMany(mappedBy = "convidador", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Convite> convitesEnviados = new ArrayList<>();
}
