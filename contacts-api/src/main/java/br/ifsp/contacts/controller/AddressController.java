package br.ifsp.contacts.controller;

import br.ifsp.contacts.dto.AddressRequest;
import br.ifsp.contacts.dto.AddressResponseDTO;
import br.ifsp.contacts.exception.ResourceNotFoundException;
import br.ifsp.contacts.model.Address;
import br.ifsp.contacts.model.Contact;
import br.ifsp.contacts.repository.AddressRepository;
import br.ifsp.contacts.repository.ContactRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/addresses")
@Tag(name = "Endereços", description = "Endpoints para gerenciamento de endereços")
public class AddressController {

    private static final Logger log = LoggerFactory.getLogger(AddressController.class);

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ContactRepository contactRepository;

    @GetMapping
    @Operation(summary = "Listar todos os endereços", description = "Retorna uma lista paginada de todos os endereços. Use os parâmetros page, size e sort para controlar a paginação e ordenação.")
    public Page<AddressResponseDTO> getAllAddress(Pageable pageable) {
        log.info("GET /api/addresses - page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return addressRepository.findAll(pageable)
                .map(AddressResponseDTO::new);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar endereço por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Endereço encontrado"),
        @ApiResponse(responseCode = "404", description = "Endereço não encontrado")
    })
    public AddressResponseDTO getAddressById(
            @Parameter(description = "ID do endereço") @PathVariable Long id) {
        log.info("GET /api/addresses/{}", id);
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado"));
        return new AddressResponseDTO(address);
    }

    @PostMapping
    @Operation(summary = "Criar novo endereço")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Endereço criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Contato não encontrado")
    })
    public AddressResponseDTO createAddress(@Valid @RequestBody AddressRequest addressRequest) {
        log.info("POST /api/addresses - contactId={}", addressRequest.getContactId());
        Contact contact = contactRepository.findById(addressRequest.getContactId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contato com id: " + addressRequest.getContactId() + " não encontrado"));

        Address address = new Address();
        address.setRua(addressRequest.getRua());
        address.setCidade(addressRequest.getCidade());
        address.setEstado(addressRequest.getEstado());
        address.setCep(addressRequest.getCep());
        address.setContact(contact);

        Address savedAddress = addressRepository.save(address);
        return new AddressResponseDTO(savedAddress);
    }
}
