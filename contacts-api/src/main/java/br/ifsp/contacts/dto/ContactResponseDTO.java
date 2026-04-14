package br.ifsp.contacts.dto;
import br.ifsp.contacts.model.Contact;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nome", "telefone", "email"})
public class ContactResponseDTO {
    private Long id;
    private String nome;
    private String telefone;
    private String email;

   public ContactResponseDTO(Contact contact){
       this.id = contact.getId();
       this.nome = contact.getNome();
       this.telefone = contact.getTelefone();
       this.email = contact.getEmail();
   }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }
}
