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
@Table(name = "user")
public class Usuario implements Serializable {

    @Id
    @Column(nullable = false)
    private String id;

    @NotBlank(message = "O nome é obrigatório")
    @Column(name = "name", nullable = false)
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "O CPF é obrigatório")
    @Column(name = "cpf", nullable = false, unique = true)
    private String cpf;

    @Column(name = "phone")
    private String telefone;

    @Builder.Default
    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerificado = false;

    @Column(name = "image")
    private String imagem;

    @Builder.Default
    @Column(name = "onboarding_completed")
    private Boolean onboardingCompleto = false;

    @Builder.Default
    @Column(name = "role")
    private String papel = "user";

    @Builder.Default
    @Column(name = "banned")
    private Boolean banido = false;

    @Column(name = "ban_reason")
    private String motivoBanimento;

    @Column(name = "ban_expires")
    private LocalDateTime banimentoExpiraEm;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
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
