package br.sst.auditoria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Estado (UF)
 * Referência para unidades federativas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "estado")
public class Estado implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Código IBGE é obrigatório")
    @Column(name = "codigo_ibge", nullable = false, unique = true)
    private Integer codigoIbge;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100)
    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @NotBlank(message = "UF é obrigatória")
    @Size(min = 2, max = 2)
    @Column(name = "uf", length = 2, nullable = false)
    private String uf;

    @OneToMany(mappedBy = "estado", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Cidade> cidades = new ArrayList<>();
}
