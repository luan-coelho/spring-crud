package br.sst.auditoria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "organizacao")
public class Organizacao implements Serializable {

    @Id
    @Column(nullable = false)
    private String id;

    @NotBlank(message = "O nome é obrigatório")
    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "logo")
    private String logo;

    @Column(name = "metadados", columnDefinition = "TEXT")
    private String metadados;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    // Relacionamentos
    @OneToMany(mappedBy = "organizacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Membro> membros = new ArrayList<>();

    @OneToMany(mappedBy = "organizacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Convite> convites = new ArrayList<>();
}
