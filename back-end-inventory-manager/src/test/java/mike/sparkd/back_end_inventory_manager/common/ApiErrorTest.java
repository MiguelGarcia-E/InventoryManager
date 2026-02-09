package mike.sparkd.back_end_inventory_manager.common;

import mike.sparkd.back_end_inventory_manager.common.api.ApiError;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ApiErrorTest {

    @Test
    void constructorConArgumentos_debeInicializarCampos() {
        String message = "Algo salió mal";
        String path = "/api/v1/test";

        ApiError error = new ApiError(
                400,
                "Bad Request",
                message,
                path
        );

        assertNotNull(error.getTimestamp(), "timestamp no debería ser null");
        assertEquals(400, error.getStatus());
        assertEquals("Bad Request", error.getError());
        assertEquals(message, error.getMessage());
        assertEquals(path, error.getPath());
    }

    @Test
    void settersYGetters_debenFuncionarCorrectamente() {
        ApiError error = new ApiError();

        Instant now = Instant.now();
        error.setTimestamp(now);
        error.setStatus(404);
        error.setError("Not Found");
        error.setMessage("Recurso no encontrado");
        error.setPath("/api/v1/404");

        assertEquals(now, error.getTimestamp());
        assertEquals(404, error.getStatus());
        assertEquals("Not Found", error.getError());
        assertEquals("Recurso no encontrado", error.getMessage());
        assertEquals("/api/v1/404", error.getPath());
    }
}
