package br.sst.auditoria.model.enums;

import lombok.Getter;

/**
 * Papéis padrões da organização
 */
@Getter
public enum Papel {
    PROPRIETARIO("Dono da organização"),
    ADMINISTRADOR("Gerencia usuários e dados"),
    AUDITOR("Realiza inspeções e auditorias"),
    CLIENTE("Cliente que contrata a auditoria"),
    MEMBRO("Membro com acesso básico");

    private final String descricao;

    Papel(String descricao) {
        this.descricao = descricao;
    }
}
