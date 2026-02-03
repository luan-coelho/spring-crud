-- Tabela para armazenar tokens de "Lembrar-me" do Spring Security
-- NOTA: O Spring Security usa esse nome padrão para a tabela
CREATE TABLE IF NOT EXISTS persistent_logins (
    username VARCHAR(64) NOT NULL,
    series VARCHAR(64) PRIMARY KEY,
    token VARCHAR(64) NOT NULL,
    last_used TIMESTAMP NOT NULL
);