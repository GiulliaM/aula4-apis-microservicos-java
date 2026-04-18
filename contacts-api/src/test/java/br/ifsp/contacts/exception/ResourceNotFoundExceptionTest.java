package br.ifsp.contacts.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ResourceNotFoundExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        String message = "Contato não encontrado";
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        assertEquals(message, exception.getMessage());
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void shouldPreserveMessageForContactNotFound() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Contato com ID: 1 nao encontrado");
        assertEquals("Contato com ID: 1 nao encontrado", exception.getMessage());
    }

    @Test
    void shouldPreserveMessageForAddressNotFound() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Endereço não encontrado");
        assertEquals("Endereço não encontrado", exception.getMessage());
    }
}
