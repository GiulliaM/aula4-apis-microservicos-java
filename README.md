CREATE DATABASE contacts_db;

-- Tabela de Contatos (Baseada na entidade Contact)
CREATE TABLE contact (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    telefone VARCHAR(15) NOT NULL,
    email VARCHAR(255) NOT NULL
);

-- Tabela de Endereços (Baseada na entidade Address)
CREATE TABLE address (
    id BIGSERIAL PRIMARY KEY,
    rua VARCHAR(255) NOT NULL,
    cidade VARCHAR(255) NOT NULL,
    estado VARCHAR(255) NOT NULL,
    cep VARCHAR(20) NOT NULL,
    contact_id BIGINT NOT NULL,
    CONSTRAINT fk_contact FOREIGN KEY (contact_id) 
        REFERENCES contact (id) 
        ON DELETE CASCADE
);
