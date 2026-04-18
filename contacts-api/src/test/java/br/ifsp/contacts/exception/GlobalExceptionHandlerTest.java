package br.ifsp.contacts.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleValidationException_shouldReturn400WithFieldMessage() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("contact", "nome", "O campo nome nao pode estar vazio");
        when(bindingResult.getFieldError()).thenReturn(fieldError);

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, String>> response = handler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("O campo nome nao pode estar vazio", response.getBody().get("Erro"));
    }

    @Test
    void handleValidationException_shouldReturnDefaultMessage_whenNoFieldError() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldError()).thenReturn(null);

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, String>> response = handler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Dados invalidos", response.getBody().get("Erro"));
    }

    @Test
    void handleResourceNotFoundException_shouldReturn404WithMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Contato não encontrado");

        ResponseEntity<Map<String, String>> response = handler.handleResourceNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Contato não encontrado", response.getBody().get("Erro"));
    }

    @Test
    void handleResourceNotFoundException_shouldReturn404ForAddressMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Endereço com ID: 5 não encontrado");

        ResponseEntity<Map<String, String>> response = handler.handleResourceNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Endereço com ID: 5 não encontrado", response.getBody().get("Erro"));
    }

    @Test
    void handleGenericException_shouldReturn500WithGenericMessage() {
        Exception exception = new RuntimeException("Erro inesperado");

        ResponseEntity<Map<String, String>> response = handler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("Erro"));
        assertEquals("Ocorreu um erro interno no servidor. Tente novamente", response.getBody().get("Erro"));
    }

    @Test
    void handleGenericException_shouldReturn500_forNullPointerException() {
        Exception exception = new NullPointerException("npe");

        ResponseEntity<Map<String, String>> response = handler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
