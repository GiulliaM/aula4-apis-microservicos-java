package br.ifsp.contacts.controller;

import br.ifsp.contacts.dto.AddressRequest;
import br.ifsp.contacts.dto.AddressResponseDTO;
import br.ifsp.contacts.exception.ResourceNotFoundException;
import br.ifsp.contacts.model.Address;
import br.ifsp.contacts.model.Contact;
import br.ifsp.contacts.repository.AddressRepository;
import br.ifsp.contacts.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private AddressController addressController;

    private Contact contact;
    private Address address;
    private final Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        contact = new Contact("João Silva", "11999999999", "joao@email.com");
        contact.setId(1L);
        address = new Address("Rua A", "São Paulo", "SP", "01310-100", contact);
        address.setId(1L);
    }

    @Test
    void getAllAddresses_shouldReturnPage() {
        Page<Address> page = new PageImpl<>(List.of(address));
        when(addressRepository.findAll(pageable)).thenReturn(page);

        Page<AddressResponseDTO> result = addressController.getAllAddress(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Rua A", result.getContent().getFirst().rua());
    }

    @Test
    void getAllAddresses_shouldReturnEmptyPage_whenNoAddresses() {
        when(addressRepository.findAll(pageable)).thenReturn(Page.empty());

        Page<AddressResponseDTO> result = addressController.getAllAddress(pageable);

        assertEquals(0, result.getTotalElements());
    }

    @Test
    void getAddressById_shouldReturnDTO_whenFound() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));

        AddressResponseDTO result = addressController.getAddressById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Rua A", result.rua());
        assertEquals("São Paulo", result.cidade());
        assertEquals("SP", result.estado());
        assertEquals("01310-100", result.cep());
    }

    @Test
    void getAddressById_shouldThrowResourceNotFoundException_whenNotFound() {
        when(addressRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> addressController.getAddressById(99L));
    }

    @Test
    void createAddress_shouldReturnDTO_afterSaving() {
        AddressRequest req = buildValidRequest(1L);

        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        AddressResponseDTO result = addressController.createAddress(req);

        assertNotNull(result);
        assertEquals("Rua A", result.rua());
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void createAddress_shouldThrowResourceNotFoundException_whenContactNotFound() {
        AddressRequest req = buildValidRequest(99L);
        when(contactRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> addressController.createAddress(req));
    }

    @Test
    void updateAddress_shouldReturnUpdatedDTO() {
        AddressRequest req = new AddressRequest();
        req.setRua("Rua Atualizada");
        req.setCidade("Campinas");
        req.setEstado("SP");
        req.setCep("13000-000");
        req.setContactId(1L);

        Address updated = new Address("Rua Atualizada", "Campinas", "SP", "13000-000", contact);
        updated.setId(1L);

        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
        when(addressRepository.save(any(Address.class))).thenReturn(updated);

        AddressResponseDTO result = addressController.updateAddress(1L, req);

        assertEquals("Rua Atualizada", result.rua());
        assertEquals("Campinas", result.cidade());
    }

    @Test
    void updateAddress_shouldThrowResourceNotFoundException_whenAddressNotFound() {
        AddressRequest req = buildValidRequest(1L);
        when(addressRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> addressController.updateAddress(99L, req));
    }

    @Test
    void updateAddress_shouldThrowResourceNotFoundException_whenContactNotFound() {
        AddressRequest req = buildValidRequest(99L);
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(contactRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> addressController.updateAddress(1L, req));
    }

    @Test
    void deleteAddress_shouldCallRepositoryDelete_whenFound() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));

        addressController.deleteAddress(1L);

        verify(addressRepository).delete(address);
    }

    @Test
    void deleteAddress_shouldThrowResourceNotFoundException_whenNotFound() {
        when(addressRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> addressController.deleteAddress(99L));
    }

    private AddressRequest buildValidRequest(Long contactId) {
        AddressRequest req = new AddressRequest();
        req.setRua("Rua A");
        req.setCidade("São Paulo");
        req.setEstado("SP");
        req.setCep("01310-100");
        req.setContactId(contactId);
        return req;
    }
}