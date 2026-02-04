package br.sst.auditoria.model.enums;

import lombok.Getter;

/**
 * Situação do Funcionário
 */
@Getter
public enum SituacaoFuncionario {
    ATIVO("Ativo"),
    AFASTADO("Afastado"),
    DESLIGADO("Desligado"),
    EM_CONTRATACAO("Em Contratação");

    private final String descricao;

    SituacaoFuncionario(String descricao) {
        this.descricao = descricao;
    }
}
