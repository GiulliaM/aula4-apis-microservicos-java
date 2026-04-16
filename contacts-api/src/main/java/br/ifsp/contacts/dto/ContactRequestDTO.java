package br.ifsp.contacts.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ContactRequestDTO {

    @NotBlank(message = "O campo nome nao pode estar vazio")
    private String nome;

    @NotBlank(message = "O campo telefone nao pode estar vazio")
    @Size(min = 8, max = 15, message = "O telefone deve ter entre 8 e 15 caracteres")
    private String telefone;

    @NotBlank(message = "O campo email nao pode estar vazio")
    @Email(message = "Digite um email valido")
    private String email;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
