package br.ifsp.contacts.controller;

import br.ifsp.contacts.dto.AddressRequest;
import br.ifsp.contacts.dto.AddressResponseDTO;
import br.ifsp.contacts.exception.ResourceNotFoundException;
import br.ifsp.contacts.model.Address;
import br.ifsp.contacts.model.Contact;
import br.ifsp.contacts.repository.AddressRepository;
import br.ifsp.contacts.repository.ContactRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ContactRepository contactRepository;

    @GetMapping
    public List<AddressResponseDTO> getAllAddress(){
        return addressRepository.findAll()
                .stream()
                .map(AddressResponseDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AddressResponseDTO getAddressById(@PathVariable Long id) {
       Address address = addressRepository.findById(id)
               .orElseThrow(()->new ResourceNotFoundException("Endereço não encontrado"));
       return new AddressResponseDTO(address);
    }

    @PostMapping
    public AddressResponseDTO createAddress(@Valid @RequestBody AddressRequest addressRequest) {
        Contact contact = contactRepository.findById(addressRequest.getContactId())
                .orElseThrow(() -> new ResourceNotFoundException("Contato com id: " + addressRequest.getContactId() + " não encontrado"));

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
