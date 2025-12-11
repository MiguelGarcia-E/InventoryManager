package mike.sparkd.back_end_inventory_manager.category;


import com.fasterxml.jackson.databind.ObjectMapper;
import mike.sparkd.back_end_inventory_manager.category.Controller.CategoryController;
import mike.sparkd.back_end_inventory_manager.category.Model.Category;
import mike.sparkd.back_end_inventory_manager.category.Service.CategoryService;
import mike.sparkd.back_end_inventory_manager.common.exception.GlobalExceptionHandler;
import mike.sparkd.back_end_inventory_manager.common.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CategoryController.class)
@Import(GlobalExceptionHandler.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    // GET /api/v1/categories
    @Test
    void list_debeRetornarListaDeCategoryReadDtoOrdenados() throws Exception {
        Category c1 = new Category("Alpha");
        c1.setId(1L);
        Category c2 = new Category("Beta");
        c2.setId(2L);

        when(categoryService.getAllCategoriesSortedByName()).thenReturn(List.of(c1, c2));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Alpha"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Beta"));
    }

    // GET /api/v1/categories/{id} OK
    @Test
    void getById_existente_debeRetornarCategoryReadDto() throws Exception {
        Category c = new Category("Cloud");
        c.setId(10L);

        when(categoryService.getCategoryById(10L)).thenReturn(c);

        mockMvc.perform(get("/api/v1/categories/{id}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("Cloud"));

        verify(categoryService, times(1)).getCategoryById(10L);
    }

    // GET /api/v1/categories/{id} NotFound desde service
    @Test
    void getById_noExistente_debeRetornar404ConApiError() throws Exception {
        when(categoryService.getCategoryById(99L))
                .thenThrow(new NotFoundException("Category 99 not found"));

        mockMvc.perform(get("/api/v1/categories/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Category 99 not found"))
                .andExpect(jsonPath("$.path").value("/api/v1/categories/99"));
    }

    // GET /api/v1/categories/{id} id inválido (violación @Positive)
    @Test
    void getById_idInvalido_debeRetornar400PorConstraintViolation() throws Exception {
        mockMvc.perform(get("/api/v1/categories/{id}", 0L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Constraint violation"))
                .andExpect(jsonPath("$.message", containsString("must be greater than 0")));
    }

    // POST /api/v1/categories OK
    @Test
    void create_valido_debeRetornar201YCategory() throws Exception {
        Category request = new Category("DevOps");
        Category saved = new Category("DevOps");
        saved.setId(1L);
        saved.setCreationDate(LocalDate.of(2024, 1, 1));
        saved.setUpdateDate(LocalDate.of(2024, 1, 1));

        when(categoryService.saveCategory(any(Category.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("DevOps"))
                .andExpect(jsonPath("$.creationDate").value("2024-01-01"))
                .andExpect(jsonPath("$.updateDate").value("2024-01-01"));

        verify(categoryService, times(1)).saveCategory(any(Category.class));
    }

    // POST /api/v1/categories inválido -> @Valid Category.name
    @Test
    void create_nombreInvalido_debeRetornar400ValidationError() throws Exception {
        // name vacío viola @NotBlank
        String jsonBody = """
                {
                  "name": "   "
                }
                """;

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.message", containsString("name")))
                .andExpect(jsonPath("$.message", containsString("blank")));
    }

    // PUT /api/v1/categories/{id} OK
    @Test
    void update_valido_debeRetornar200YCategoryActualizada() throws Exception {
        Category request = new Category("Networking");
        // el controller le va a setear el id del path antes de llamar al service

        Category updated = new Category("Networking Updated");
        updated.setId(5L);

        when(categoryService.updateCategory(any(Category.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/categories/{id}", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.name").value("Networking Updated"));

        verify(categoryService, times(1)).updateCategory(any(Category.class));
    }

    // PUT /api/v1/categories/{id} id inválido -> @Positive
    @Test
    void update_idInvalido_debeRetornar400PorConstraintViolation() throws Exception {
        Category request = new Category("Algo");

        mockMvc.perform(put("/api/v1/categories/{id}", -1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Constraint violation"));
    }

    // DELETE /api/v1/categories/{id} OK
    @Test
    void delete_existente_debeRetornar204() throws Exception {
        when(categoryService.deleteCategoryById(10L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/categories/{id}", 10L))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategoryById(10L);
    }

    // DELETE /api/v1/categories/{id} no existente -> NotFoundException
    @Test
    void delete_noExistente_debeRetornar404ConApiError() throws Exception {
        when(categoryService.deleteCategoryById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/categories/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Category 99 not found"))
                .andExpect(jsonPath("$.path").value("/api/v1/categories/99"));

        verify(categoryService, times(1)).deleteCategoryById(99L);
    }

    // DELETE /api/v1/categories/{id} id inválido -> @Positive
    @Test
    void delete_idInvalido_debeRetornar400PorConstraintViolation() throws Exception {
        mockMvc.perform(delete("/api/v1/categories/{id}", 0L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Constraint violation"));
    }
}