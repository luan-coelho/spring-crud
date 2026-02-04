package br.sst.auditoria.dto.auth;

public record AuthResponse(
    String token,
    String type,
    String id,
    String nome,
    String email,
    String papel,
    String imagem
) {
    public static AuthResponse of(String token, String id, String nome, String email, String papel, String imagem) {
        return new AuthResponse(token, "Bearer", id, nome, email, papel, imagem);
    }
}
