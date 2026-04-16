package br.ifsp.contacts.controller;

import br.ifsp.contacts.dto.AddressResponseDTO;
import br.ifsp.contacts.dto.ContactRequestDTO;
import br.ifsp.contacts.dto.ContactResponseDTO;
import br.ifsp.contacts.exception.ResourceNotFoundException;
import br.ifsp.contacts.model.Contact;
import br.ifsp.contacts.repository.AddressRepository;
import br.ifsp.contacts.repository.ContactRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

@RestController
@RequestMapping("/api/contacts")
@Tag(name = "Contatos", description = "Endpoints para gerenciamento de contatos")
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    @GetMapping
    @Operation(summary = "Listar todos os contatos", description = "Retorna uma lista paginada de todos os contatos. Use os parâmetros page, size e sort para controlar a paginação e ordenação.")
    public Page<ContactResponseDTO> getAllContacts(Pageable pageable) {
        return contactRepository.findAll(pageable)
                .map(ContactResponseDTO::new);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar contato por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contato encontrado"),
        @ApiResponse(responseCode = "404", description = "Contato não encontrado")
    })
    public ContactResponseDTO getContactId(
            @Parameter(description = "ID do contato") @PathVariable Long id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato com ID: " + id + " nao encontrado"));
        return new ContactResponseDTO(contact);
    }

    @PostMapping
    @Operation(summary = "Criar novo contato")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contato criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ContactResponseDTO createContact(@Valid @RequestBody ContactRequestDTO dto) {
        Contact contact = new Contact(dto.getNome(), dto.getTelefone(), dto.getEmail());
        Contact savedContact = contactRepository.save(contact);
        return new ContactResponseDTO(savedContact);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar contato completo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contato atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Contato não encontrado")
    })
    public ContactResponseDTO updateContact(
            @Parameter(description = "ID do contato") @PathVariable Long id,
            @Valid @RequestBody ContactRequestDTO dto) {
        Contact existingContact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato com ID: " + id + " nao encontrado"));

        existingContact.setNome(dto.getNome());
        existingContact.setTelefone(dto.getTelefone());
        existingContact.setEmail(dto.getEmail());

        Contact savedContact = contactRepository.save(existingContact);
        return new ContactResponseDTO(savedContact);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar contato")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contato deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Contato não encontrado")
    })
    public void deleteContact(
            @Parameter(description = "ID do contato") @PathVariable Long id) {
        Contact existingContact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nao foi possivel deletar contato ID: " + id + ". Contato nao encontrado"));
        contactRepository.delete(existingContact);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar contatos por nome", description = "Busca paginada de contatos pelo nome (case-insensitive). Use os parâmetros page, size e sort para controlar a paginação e ordenação.")
    public Page<ContactResponseDTO> getContactByName(
            @Parameter(description = "Nome para busca") @RequestParam("name") String nome,
            Pageable pageable) {
        return contactRepository.findByNomeContainingIgnoreCase(nome, pageable)
                .map(ContactResponseDTO::new);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualização parcial do contato", description = "Atualiza apenas os campos informados no corpo da requisição")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contato atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Contato não encontrado")
    })
    public ContactResponseDTO patchUpdateContact(
            @Parameter(description = "ID do contato") @PathVariable Long id,
            @RequestBody ContactRequestDTO dto) {
        Contact existingContact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato com ID: " + id + " nao encontrado"));

        if (dto.getNome() != null && !dto.getNome().trim().isEmpty()) {
            existingContact.setNome(dto.getNome());
        }
        if (dto.getTelefone() != null && !dto.getTelefone().trim().isEmpty()) {
            existingContact.setTelefone(dto.getTelefone());
        }
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            existingContact.setEmail(dto.getEmail());
        }

        Contact savedContact = contactRepository.save(existingContact);
        return new ContactResponseDTO(savedContact);
    }

    @GetMapping("/{id}/addresses")
    @Operation(summary = "Listar endereços de um contato", description = "Retorna lista paginada de endereços do contato. Use os parâmetros page, size e sort para controlar a paginação e ordenação.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Endereços encontrados"),
        @ApiResponse(responseCode = "404", description = "Contato não encontrado")
    })
    public Page<AddressResponseDTO> getAddressesByContact(
            @Parameter(description = "ID do contato") @PathVariable Long id,
            Pageable pageable) {
        if (!contactRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contato com ID: " + id + " nao encontrado");
        }
        return addressRepository.findByContact_Id(id, pageable)
                .map(AddressResponseDTO::new);
    }
}
