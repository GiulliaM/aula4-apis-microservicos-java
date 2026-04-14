package br.ifsp.contacts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AddressRequest {

    @NotBlank(message = "O campo rua não pode estar vazio")
    private String rua;

    @NotBlank(message = "O campo cidade não pode estar vazio")
    private String cidade;

    @NotBlank(message = "O campo estado não pode estar vazio")
    private String estado;

    @NotBlank(message = "O campo CEP não pode estar vazio")
    private String cep;

    @NotNull(message = "O ID do contato é obrigatório")
    private Long contactId;

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }
}
