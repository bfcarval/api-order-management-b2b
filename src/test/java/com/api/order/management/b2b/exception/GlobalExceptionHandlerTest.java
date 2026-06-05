package com.api.order.management.b2b.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        this.exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("1. Deve interceptar ResourceNotFoundException e retornar HTTP 404 com os dados limpos")
    void shouldHandleResourceNotFoundException() {
        var exception = new ResourceNotFoundException("Pedido não encontrado");

        var response = exceptionHandler.handleResourceNotFound(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        var body = response.getBody();
        assertNotNull(body);
        assertEquals(404, body.get("status"));
        assertEquals("Not Found", body.get("error"));
        assertEquals("Pedido não encontrado", body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    @DisplayName("2. Deve interceptar DatabaseException e retornar HTTP 500 com mensagem de infraestrutura")
    void shouldHandleDatabaseException() {
        var exception = new DatabaseException("Erro técnico de conexão no pool de dados", new RuntimeException());

        var response = exceptionHandler.handleDatabaseException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        var body = response.getBody();
        assertNotNull(body);
        assertEquals("Erro de Infraestrutura", body.get("error"));
        assertEquals("Erro técnico de conexão no pool de dados", body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    @DisplayName("3. Deve interceptar BusinessException e retornar HTTP 400 com a quebra de regra de negócio")
    void shouldHandleBusinessException() {
        var exception = new BusinessException("Limite de crédito do parceiro comercial excedido");

        var response = exceptionHandler.handleBusiness(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        var body = response.getBody();
        assertNotNull(body);
        assertEquals("Limite de crédito do parceiro comercial excedido", body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    @DisplayName("4. Deve interceptar MethodArgumentNotValidException e retornar HTTP 422 mapeando os campos com erro")
    void shouldHandleMethodArgumentNotValidException() {
        var methodParameter = mock(MethodParameter.class);
        var bindingResult = new BeanPropertyBindingResult(new Object(), "orderRequest");
        bindingResult.addError(new FieldError("orderRequest", "partnerId", "ID do parceiro é obrigatório"));
        bindingResult.addError(new FieldError("orderRequest", "items", "A lista de itens não pode estar vazia"));

        var exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        var response = exceptionHandler.handleValidation(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());

        var errorsMap = response.getBody();
        assertNotNull(errorsMap);
        assertEquals(2, errorsMap.size());
        assertEquals("ID do parceiro é obrigatório", errorsMap.get("partnerId"));
        assertEquals("A lista de itens não pode estar vazia", errorsMap.get("items"));
    }

    @Test
    @DisplayName("5. Deve interceptar MissingRequestHeaderException e retornar HTTP 400 detalhando a ausência do cabeçalho")
    void shouldHandleMissingRequestHeaderException() {
        var methodParameter = mock(MethodParameter.class);
        var exception = new MissingRequestHeaderException("X-Idempotency-Key", methodParameter);

        var response = exceptionHandler.handleMissingHeader(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        var body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        assertEquals("Header Obrigatório Ausente", body.get("error"));

        var expectedMessage = "O cabeçalho 'X-Idempotency-Key' é estritamente necessário para garantir a segurança financeira desta operação.";
        assertEquals(expectedMessage, body.get("message"));
        assertNotNull(body.get("timestamp"));
    }
}
