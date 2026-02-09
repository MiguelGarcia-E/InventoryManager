package mike.sparkd.back_end_inventory_manager.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import mike.sparkd.back_end_inventory_manager.common.api.ApiError;
import mike.sparkd.back_end_inventory_manager.common.exception.BadRequestException;
import mike.sparkd.back_end_inventory_manager.common.exception.ConflictException;
import mike.sparkd.back_end_inventory_manager.common.exception.GlobalExceptionHandler;
import mike.sparkd.back_end_inventory_manager.common.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/api/v1/test");
    }

    @Test
    void handleNotFound_debeRetornar404YApiErrorCorrecto() {
        NotFoundException ex = new NotFoundException("Recurso no encontrado");

        ResponseEntity<ApiError> response = handler.handleNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ApiError body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.NOT_FOUND.value(), body.getStatus());
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), body.getError());
        assertEquals("Recurso no encontrado", body.getMessage());
        assertEquals("/api/v1/test", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleBadRequest_debeRetornar400YApiErrorCorrecto() {
        BadRequestException ex = new BadRequestException("Bad request msg");

        ResponseEntity<ApiError> response = handler.handleBadRequest(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiError body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), body.getError());
        assertEquals("Bad request msg", body.getMessage());
        assertEquals("/api/v1/test", body.getPath());
    }

    @Test
    void handleConflict_debeRetornar409YApiErrorCorrecto() {
        ConflictException ex = new ConflictException("Nombre ya existe");

        ResponseEntity<ApiError> response = handler.handleConflict(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ApiError body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.CONFLICT.value(), body.getStatus());
        assertEquals(HttpStatus.CONFLICT.getReasonPhrase(), body.getError());
        assertEquals("Nombre ya existe", body.getMessage());
        assertEquals("/api/v1/test", body.getPath());
    }

    @Test
    void handleMethodArgumentNotValid_debeMapearPrimerFieldError() {
        // mock de MethodArgumentNotValidException + BindingResult
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError(
                "miDto",
                "nombre",
                "must not be blank"
        );

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ApiError> response = handler.handleMethodArgumentNotValid(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiError body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
        assertEquals("Validation error", body.getError());
        assertEquals("nombre must not be blank", body.getMessage());
        assertEquals("/api/v1/test", body.getPath());
    }

    @Test
    void handleMethodArgumentNotValid_sinFieldErrors_usaMensajePorDefecto() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        ResponseEntity<ApiError> response = handler.handleMethodArgumentNotValid(ex, request);

        ApiError body = response.getBody();
        assertNotNull(body);
        assertEquals("Validation error", body.getMessage());
    }

    @Test
    void handleConstraintViolation_debeMapearPrimerViolation() {
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        Path propertyPath = mock(Path.class);

        when(propertyPath.toString()).thenReturn("id");
        when(violation.getPropertyPath()).thenReturn(propertyPath);
        when(violation.getMessage()).thenReturn("must be positive");
        when(ex.getConstraintViolations()).thenReturn(Set.of(violation));

        ResponseEntity<ApiError> response = handler.handleConstraintViolation(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiError body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
        assertEquals("Constraint violation", body.getError());
        assertEquals("id must be positive", body.getMessage());
        assertEquals("/api/v1/test", body.getPath());
    }

    @Test
    void handleConstraintViolation_sinViolations_usaMensajePorDefecto() {
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        when(ex.getConstraintViolations()).thenReturn(Set.of());

        ResponseEntity<ApiError> response = handler.handleConstraintViolation(ex, request);

        ApiError body = response.getBody();
        assertNotNull(body);
        assertEquals("Constraint violation", body.getMessage());
    }

    @Test
    void handleIllegalArgument_debeRetornar400YBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("arg inválido");

        ResponseEntity<ApiError> response = handler.handleIllegalArgument(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiError body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), body.getError());
        assertEquals("arg inválido", body.getMessage());
        assertEquals("/api/v1/test", body.getPath());
    }

    @Test
    void handleGeneric_debeRetornar500YMensajeGenerico() {
        Exception ex = new Exception("cualquier cosa");

        ResponseEntity<ApiError> response = handler.handleGeneric(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ApiError body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.getStatus());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), body.getError());
        assertEquals("Unexpected error", body.getMessage());
        assertEquals("/api/v1/test", body.getPath());
    }
}
