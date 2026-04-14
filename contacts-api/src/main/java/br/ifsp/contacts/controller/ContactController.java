package br.ifsp.contacts.controller;

import br.ifsp.contacts.dto.ContactResponseDTO;
import br.ifsp.contacts.exception.ResourceNotFoundException;
import br.ifsp.contacts.model.Address;
import br.ifsp.contacts.model.Contact;
import br.ifsp.contacts.repository.AddressRepository;
import br.ifsp.contacts.repository.ContactRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    @GetMapping
    public List<ContactResponseDTO> getAllContacts() {
        return contactRepository.findAll().stream()
                .map(ContactResponseDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ContactResponseDTO getContactId(@PathVariable Long id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato com ID: " + id + " nao encontrado"));
        return new ContactResponseDTO(contact);
    }

    @PostMapping
    public ContactResponseDTO createContact(@Valid @RequestBody Contact contact) {
        Contact savedContact = contactRepository.save(contact);
        return new ContactResponseDTO(savedContact);
    }

    @PutMapping("/{id}")
    public ContactResponseDTO updateContact(@PathVariable Long id, @Valid @RequestBody Contact updateContact) {
        Contact existingContact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato com ID: " + id + " nao encontrado"));

        existingContact.setNome(updateContact.getNome());
        existingContact.setTelefone(updateContact.getTelefone());
        existingContact.setEmail(updateContact.getEmail());

        Contact savedContact = contactRepository.save(existingContact);
        return new ContactResponseDTO(savedContact);
    }

    @DeleteMapping("/{id}")
    public void deleteContact(@PathVariable Long id) {
        Contact existingContact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nao foi possivel deletar contato ID: " + id + ". Contato nao encontrado"));

        contactRepository.delete(existingContact);
    }

    @GetMapping("/search")
    public List<ContactResponseDTO> getContactByName(@RequestParam("name") String nome) {
        List<Contact> contacts = contactRepository.findByNomeContainingIgnoreCase(nome);
        if (contacts.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum contato encontrado com o nome: " + nome);
        }

        return contacts.stream()
                .map(ContactResponseDTO::new)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{id}")
    public ContactResponseDTO patchUpdateContact(@PathVariable Long id, @RequestBody Contact partialContact) {
        Contact existingContact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato com ID: " + id + " nao encontrado"));

        if (partialContact.getNome() != null && !partialContact.getNome().isBlank()) {
            existingContact.setNome(partialContact.getNome());
        }
        if (partialContact.getTelefone() != null && !partialContact.getTelefone().isBlank()) {
            existingContact.setTelefone(partialContact.getTelefone());
        }
        if (partialContact.getEmail() != null && !partialContact.getEmail().isBlank()) {
            existingContact.setEmail(partialContact.getEmail());
        }

        Contact savedContact = contactRepository.save(existingContact);
        return new ContactResponseDTO(savedContact);
    }

    @GetMapping("/{id}/addresses")
    public List<Address> getAddressesByContact(@PathVariable Long id) {
        if (!contactRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contato com ID: " + id + " nao encontrado");
        }
        return addressRepository.findByContact_Id(id);
    }
}
