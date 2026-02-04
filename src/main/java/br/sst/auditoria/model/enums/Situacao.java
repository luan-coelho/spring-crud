package br.sst.auditoria.model.enums;

import lombok.Getter;

/**
 * Situação cadastral geral (Ativo/Inativo)
 */
@Getter
public enum Situacao {
    ATIVO("Ativo"),
    INATIVO("Inativo"),
    ARQUIVADO("Arquivado");

    private final String descricao;

    Situacao(String descricao) {
        this.descricao = descricao;
    }
}
