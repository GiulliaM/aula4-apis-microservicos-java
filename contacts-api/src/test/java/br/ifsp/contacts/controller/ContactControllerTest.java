package br.ifsp.contacts.controller;

import br.ifsp.contacts.dto.AddressResponseDTO;
import br.ifsp.contacts.dto.ContactRequestDTO;
import br.ifsp.contacts.dto.ContactResponseDTO;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactControllerTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private ContactController contactController;

    private Contact contact;
    private final Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        contact = new Contact("João Silva", "11999999999", "joao@email.com");
        contact.setId(1L);
    }

    @Test
    void getAllContacts_shouldReturnPageOfContactDTOs() {
        Page<Contact> page = new PageImpl<>(List.of(contact));
        when(contactRepository.findAll(pageable)).thenReturn(page);

        Page<ContactResponseDTO> result = contactController.getAllContacts(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("João Silva", result.getContent().get(0).getNome());
    }

    @Test
    void getAllContacts_shouldReturnEmptyPage_whenNoContacts() {
        when(contactRepository.findAll(pageable)).thenReturn(Page.empty());

        Page<ContactResponseDTO> result = contactController.getAllContacts(pageable);

        assertEquals(0, result.getTotalElements());
    }

    @Test
    void getContactById_shouldReturnDTO_whenFound() {
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));

        ContactResponseDTO result = contactController.getContactId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("João Silva", result.getNome());
        assertEquals("11999999999", result.getTelefone());
        assertEquals("joao@email.com", result.getEmail());
    }

    @Test
    void getContactById_shouldThrowResourceNotFoundException_whenNotFound() {
        when(contactRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> contactController.getContactId(99L));
    }

    @Test
    void createContact_shouldReturnDTO_afterSaving() {
        ContactRequestDTO dto = buildValidDTO();
        when(contactRepository.save(any(Contact.class))).thenReturn(contact);

        ContactResponseDTO result = contactController.createContact(dto);

        assertNotNull(result);
        assertEquals("João Silva", result.getNome());
        verify(contactRepository).save(any(Contact.class));
    }

    @Test
    void updateContact_shouldReturnUpdatedDTO() {
        ContactRequestDTO dto = new ContactRequestDTO();
        dto.setNome("João Atualizado");
        dto.setTelefone("11888888888");
        dto.setEmail("joao.novo@email.com");

        Contact updated = new Contact("João Atualizado", "11888888888", "joao.novo@email.com");
        updated.setId(1L);

        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
        when(contactRepository.save(any(Contact.class))).thenReturn(updated);

        ContactResponseDTO result = contactController.updateContact(1L, dto);

        assertEquals("João Atualizado", result.getNome());
        assertEquals("joao.novo@email.com", result.getEmail());
    }

    @Test
    void updateContact_shouldThrowResourceNotFoundException_whenNotFound() {
        ContactRequestDTO dto = buildValidDTO();
        when(contactRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> contactController.updateContact(99L, dto));
    }

    @Test
    void deleteContact_shouldCallRepositoryDelete_whenFound() {
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));

        contactController.deleteContact(1L);

        verify(contactRepository).delete(contact);
    }

    @Test
    void deleteContact_shouldThrowResourceNotFoundException_whenNotFound() {
        when(contactRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> contactController.deleteContact(99L));
    }

    @Test
    void searchContactByName_shouldReturnMatchingPage() {
        Page<Contact> page = new PageImpl<>(List.of(contact));
        when(contactRepository.findByNomeContainingIgnoreCase(eq("João"), eq(pageable))).thenReturn(page);

        Page<ContactResponseDTO> result = contactController.getContactByName("João", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("João Silva", result.getContent().get(0).getNome());
    }

    @Test
    void searchContactByName_shouldReturnEmptyPage_whenNoMatch() {
        when(contactRepository.findByNomeContainingIgnoreCase(eq("inexistente"), eq(pageable)))
                .thenReturn(Page.empty());

        Page<ContactResponseDTO> result = contactController.getContactByName("inexistente", pageable);

        assertEquals(0, result.getTotalElements());
    }

    @Test
    void patchContact_shouldUpdateOnlyNome_whenOnlyNomeProvided() {
        ContactRequestDTO dto = new ContactRequestDTO();
        dto.setNome("João Parcial");

        Contact updated = new Contact("João Parcial", "11999999999", "joao@email.com");
        updated.setId(1L);

        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
        when(contactRepository.save(any(Contact.class))).thenReturn(updated);

        ContactResponseDTO result = contactController.patchUpdateContact(1L, dto);

        assertEquals("João Parcial", result.getNome());
        assertEquals("joao@email.com", result.getEmail());
    }

    @Test
    void patchContact_shouldNotUpdateBlankFields() {
        ContactRequestDTO dto = new ContactRequestDTO();
        dto.setNome("  ");

        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
        when(contactRepository.save(any(Contact.class))).thenReturn(contact);

        ContactResponseDTO result = contactController.patchUpdateContact(1L, dto);

        assertEquals("João Silva", result.getNome());
    }

    @Test
    void patchContact_shouldThrowResourceNotFoundException_whenNotFound() {
        ContactRequestDTO dto = new ContactRequestDTO();
        dto.setNome("Novo Nome");

        when(contactRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> contactController.patchUpdateContact(99L, dto));
    }

    @Test
    void getAddressesByContact_shouldReturnPage_whenContactExists() {
        Address address = new Address("Rua A", "São Paulo", "SP", "01310-100", contact);
        address.setId(1L);
        Page<Address> page = new PageImpl<>(List.of(address));

        when(contactRepository.existsById(1L)).thenReturn(true);
        when(addressRepository.findByContact_Id(eq(1L), eq(pageable))).thenReturn(page);

        Page<AddressResponseDTO> result = contactController.getAddressesByContact(1L, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Rua A", result.getContent().get(0).rua());
    }

    @Test
    void getAddressesByContact_shouldThrowResourceNotFoundException_whenContactNotFound() {
        when(contactRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> contactController.getAddressesByContact(99L, pageable));
    }

    private ContactRequestDTO buildValidDTO() {
        ContactRequestDTO dto = new ContactRequestDTO();
        dto.setNome("João Silva");
        dto.setTelefone("11999999999");
        dto.setEmail("joao@email.com");
        return dto;
    }
}