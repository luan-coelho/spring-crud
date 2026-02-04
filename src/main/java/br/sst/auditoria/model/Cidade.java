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

/**
 * Entidade Cidade (Município)
 * Referência para municípios brasileiros
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "cidade",
    indexes = {
        @Index(name = "idx_cidade_estado_id", columnList = "estado_id")
    }
)
public class Cidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Código IBGE é obrigatório")
    @Column(name = "codigo_ibge", nullable = false, unique = true)
    private Integer codigoIbge;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 150)
    @Column(name = "nome", length = 150, nullable = false)
    private String nome;

    @NotNull(message = "Estado é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", nullable = false)
    private Estado estado;
}
