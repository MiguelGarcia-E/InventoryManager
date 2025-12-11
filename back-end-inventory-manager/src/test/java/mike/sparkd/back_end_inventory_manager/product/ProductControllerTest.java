package mike.sparkd.back_end_inventory_manager.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;

import mike.sparkd.back_end_inventory_manager.common.exception.ConflictException;
import mike.sparkd.back_end_inventory_manager.common.exception.GlobalExceptionHandler;
import mike.sparkd.back_end_inventory_manager.common.exception.NotFoundException;
import mike.sparkd.back_end_inventory_manager.product.Controller.ProductController;
import mike.sparkd.back_end_inventory_manager.product.Model.CategoryInventorySummary;
import mike.sparkd.back_end_inventory_manager.product.Model.PageResponse;
import mike.sparkd.back_end_inventory_manager.product.Model.Product;
import mike.sparkd.back_end_inventory_manager.product.Service.ProductService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
public class ProductControllerTest {

    private static final String END_POINT_PATH = "/api/v1/products";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    // ===================== GET =====================

    @Test
    void getSearchedProducts_returns200_whenDefaults() throws Exception {
        Product a = new Product("A", "Amazon", 10.0F, LocalDate.now().plusMonths(1L), 10);
        Product b = new Product("B", "Bamazon", 10.0F, LocalDate.now().plusMonths(1L), 10);
        a.setId(1L);
        b.setId(2L);

        PageResponse<Product> page = new PageResponse<>(List.of(a, b), 1, 10, 2L);

        Mockito.when(productService.search(
                1,      // page default
                10,     // size default
                null,   // name
                null,   // category
                "all",  // availability default
                "id",   // sortBy default
                "asc"   // direction default
        )).thenReturn(page);

        mockMvc.perform(get(END_POINT_PATH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("A"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].name").value("B"));
    }

    @Test
    void getInventorySummaryByCategory_returns200_andList() throws Exception {
        List<CategoryInventorySummary> metrics = List.of(
                new CategoryInventorySummary("Cloud", 10, 1000.0, 100.0),
                new CategoryInventorySummary("DevOps", 5, 500.0, 100.0)
        );

        Mockito.when(productService.getAllCategoryMetrics()).thenReturn(metrics);

        mockMvc.perform(get(END_POINT_PATH + "/metrics")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].category").value("Cloud"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].totalUnitsInStock").value(10));
    }

    // ===================== DELETE =====================

    @Test
    void deleteProduct_returns204_becauseDeletingExistingProduct() throws Exception {
        long pathId = 1L;
        Mockito.when(this.productService.deleteProductById(pathId)).thenReturn(true);

        this.mockMvc.perform(delete(END_POINT_PATH + "/{id}", pathId))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    @Test
    void deleteProduct_returns404_becauseDeletingMissingProduct() throws Exception {
        long pathId = 1L;
        Mockito.when(this.productService.deleteProductById(pathId)).thenReturn(false);

        this.mockMvc.perform(delete(END_POINT_PATH + "/{id}", pathId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Test
    void deleteProduct_returns404_afterDeletingTwiceSameProduct() throws Exception {
        long pathId = 1L;
        Mockito.when(this.productService.deleteProductById(pathId))
                .thenReturn(true, false);

        this.mockMvc.perform(delete(END_POINT_PATH + "/{id}", pathId))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.content().string(""));

        this.mockMvc.perform(delete(END_POINT_PATH + "/{id}", pathId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    // ===================== PUT =====================

    @Test
    void putProduct_returns200_becauseGoodPut() throws Exception {
        long pathId = 1L;
        Product incoming = new Product("AZ-900", "Certificación AWS", 100.0F,
                LocalDate.of(2026, 6, 30), 1);
        incoming.setId(pathId);

        Product updated = new Product("AZ-900", "Certificación AWS", 100.0F,
                LocalDate.of(2026, 6, 30), 1);
        updated.setId(pathId);

        Mockito.when(this.productService.updateProduct(ArgumentMatchers.any(Product.class)))
                .thenReturn(updated);

        this.mockMvc.perform(put(END_POINT_PATH + "/{id}", pathId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(incoming)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void putProduct_returns200_becauseOverridesIdFromPath() throws Exception {
        long pathId = 1L;
        Product incoming = new Product("AZ-900", "Certificación AWS", 100.0F,
                LocalDate.of(2026, 6, 30), 1);
        incoming.setId(2L);

        Product updated = new Product("AZ-900", "Certificación AWS", 100.0F,
                LocalDate.of(2026, 6, 30), 1);
        updated.setId(pathId);

        Mockito.when(this.productService.updateProduct(ArgumentMatchers.any(Product.class)))
                .thenReturn(updated);

        this.mockMvc.perform(put(END_POINT_PATH + "/{id}", pathId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(incoming)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }

    @Test
    void putProduct_returns200_becauseLackOfExpirationDate() throws Exception {
        long pathId = 1L;
        Product incoming = new Product("AZ-900", "Certificación AWS", 100.0F, 1);
        incoming.setId(pathId);

        Product updated = new Product("AZ-900", "Certificación AWS", 100.0F, 1);
        updated.setId(pathId);

        Mockito.when(this.productService.updateProduct(ArgumentMatchers.any(Product.class)))
                .thenReturn(updated);

        this.mockMvc.perform(put(END_POINT_PATH + "/{id}", pathId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(incoming)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void putProduct_returns200_becauseOfSizeOneName() throws Exception {
        long pathId = 1L;
        Product incoming = new Product("A", "Certificación AWS", 100.0F, 1);
        incoming.setId(pathId);

        Product updated = new Product("A", "Certificación AWS", 100.0F, 1);
        updated.setId(pathId);

        Mockito.when(this.productService.updateProduct(ArgumentMatchers.any(Product.class)))
                .thenReturn(updated);

        this.mockMvc.perform(put(END_POINT_PATH + "/{id}", pathId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(incoming)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void putProduct_returns404_whenServiceThrowsNotFound() throws Exception {
        long pathId = 99L;
        Product incoming = new Product("X", "Cat", 10.0F, LocalDate.now().plusDays(1), 1);
        incoming.setId(pathId);

        Mockito.when(productService.updateProduct(ArgumentMatchers.any(Product.class)))
                .thenThrow(new NotFoundException("Product not found"));

        this.mockMvc.perform(put(END_POINT_PATH + "/{id}", pathId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incoming)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(404));
    }

    @Test
    void putProduct_returns409_whenServiceThrowsConflict() throws Exception {
        long pathId = 1L;
        Product incoming = new Product("Duplicated", "Cat", 10.0F, LocalDate.now().plusDays(1), 1);
        incoming.setId(pathId);

        Mockito.when(productService.updateProduct(ArgumentMatchers.any(Product.class)))
                .thenThrow(new ConflictException("Conflict"));

        this.mockMvc.perform(put(END_POINT_PATH + "/{id}", pathId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incoming)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(409));
    }

    // ===================== POST =====================

    @Test
    void saveProduct_returns201_becauseGoodProduct() throws Exception {
        Product incoming = new Product(
                "AWS Cloud Practitioner (CLF-C02) - Exam Voucher",
                "Certificación AWS",
                100.0F,
                LocalDate.of(2026, 6, 30),
                1
        );
        Product created = new Product(
                "AWS Cloud Practitioner (CLF-C02) - Exam Voucher",
                "Certificación AWS",
                100.0F,
                LocalDate.of(2026, 6, 30),
                1
        );
        created.setId(1L);

        Mockito.when(this.productService.saveProduct(ArgumentMatchers.any(Product.class)))
                .thenReturn(created);

        this.mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(incoming)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void saveProduct_returns201_evenWithoutExpirationDate() throws Exception {
        Product incoming = new Product(
                "AWS Cloud Practitioner (CLF-C02) - Exam Voucher",
                "Certificación AWS",
                100.0F,
                1
        );
        Product created = new Product(
                "AWS Cloud Practitioner (CLF-C02) - Exam Voucher",
                "Certificación AWS",
                100.0F,
                1
        );
        created.setId(1L);

        Mockito.when(this.productService.saveProduct(ArgumentMatchers.any(Product.class)))
                .thenReturn(created);

        this.mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(incoming)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void saveProduct_returns400_becauseNegativePrice() throws Exception {
        Product product = new Product(
                "AWS Cloud Practitioner (CLF-C02) - Exam Voucher",
                "Certificación AWS",
                -100.0F,
                LocalDate.of(2026, 6, 30),
                0
        );

        String body = this.objectMapper.writeValueAsString(product);

        this.mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void saveProduct_returns400_becausePastExpirationDate() throws Exception {
        Product product = new Product(
                "AWS Cloud Practitioner (CLF-C02) - Exam Voucher",
                "Certificación AWS",
                100.0F,
                LocalDate.of(2024, 6, 30),
                0
        );

        String body = this.objectMapper.writeValueAsString(product);

        this.mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void saveProduct_returns400_becausePastExpirationDateByOneDay() throws Exception {
        Product product = new Product(
                "AWS Cloud Practitioner (CLF-C02) - Exam Voucher",
                "Certificación AWS",
                100.0F,
                LocalDate.now().minusDays(1L),
                0
        );

        String body = this.objectMapper.writeValueAsString(product);

        this.mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void saveProduct_returns400_becauseNegativeStock() throws Exception {
        Product product = new Product(
                "AWS Cloud Practitioner (CLF-C02) - Exam Voucher",
                "Certificación AWS",
                100.0F,
                LocalDate.of(2026, 6, 30),
                -1
        );

        String body = this.objectMapper.writeValueAsString(product);

        this.mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void saveProduct_returns400_becauseNameExceedingCharacterLimit() throws Exception {
        Product product = new Product(
                "This is a deliberately long string created to fulfill the request of being more than 120 characters in length. It contains a variety of words and spaces, ensuring it easily surpasses the specified character count. The purpose of this string is purely demonstrative, illustrating the ability to generate text of a significant length as requested by the user",
                "Certificación AWS",
                100.0F,
                LocalDate.of(2026, 6, 30),
                12
        );

        String body = this.objectMapper.writeValueAsString(product);

        this.mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void saveProduct_returns400_becauseNoName() throws Exception {
        String body = """
                {
                  "category": "Certificación AWS",
                  "unitPrice": 100.0,
                  "expirationDate": "2026-06-30",
                  "stock": 1
                }
                """;

        this.mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void saveProduct_returns400_becauseBlankName() throws Exception {
        Product product = new Product(
                "",
                "Certificación AWS",
                100.0F,
                LocalDate.of(2026, 6, 30),
                1
        );

        String body = this.objectMapper.writeValueAsString(product);

        this.mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void saveProduct_returns400_becauseBlankCategory() throws Exception {
        Product product = new Product(
                "AWS CERTIFICATION",
                "",
                100.0F,
                LocalDate.of(2026, 6, 30),
                1
        );

        String body = this.objectMapper.writeValueAsString(product);

        this.mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void saveProduct_returns400_becauseNoCategory() throws Exception {
        String body = """
                {
                  "name": "AWS CERTIFICATION",
                  "unitPrice": 100.0,
                  "expirationDate": "2026-06-30",
                  "stock": 1
                }
                """;

        this.mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
