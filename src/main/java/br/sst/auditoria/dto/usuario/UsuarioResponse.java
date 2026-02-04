package br.sst.auditoria.dto.usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
    String id,
    String nome,
    String email,
    String cpf,
    String telefone,
    Boolean emailVerificado,
    String imagem,
    Boolean onboardingCompleto,
    String papel,
    Boolean banido,
    String motivoBanimento,
    LocalDateTime banimentoExpiraEm,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {}
