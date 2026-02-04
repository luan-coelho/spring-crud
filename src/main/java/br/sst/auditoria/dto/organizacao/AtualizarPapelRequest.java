package br.sst.auditoria.dto.organizacao;

/**
 * DTO para atualizar papel de organização
 */
public record AtualizarPapelRequest(
    String papelNome,
    String papelId,
    String organizacaoId,
    String novoNome,
    String permissao
) {}
