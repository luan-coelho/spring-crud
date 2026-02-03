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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "verification", indexes = {
        @Index(name = "verification_identifier_idx", columnList = "identifier")
})
public class Verificacao implements Serializable {

    @Id
    @Column(nullable = false)
    private String id;

    @NotBlank(message = "O identificador é obrigatório")
    @Column(name = "identifier", nullable = false)
    private String identificador;

    @NotBlank(message = "O valor é obrigatório")
    @Column(name = "value", nullable = false)
    private String valor;

    @NotNull(message = "A data de expiração é obrigatória")
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiraEm;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime atualizadoEm;
}
