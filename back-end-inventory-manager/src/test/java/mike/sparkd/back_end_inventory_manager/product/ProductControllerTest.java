
package mike.sparkd.back_end_inventory_manager.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest({ProductController.class})
public class ProductControllerTest {
    private static final String END_POINT_PATH = "/api/v1/products";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ProductService productService;

    @Test
    void getFilteredAndPaginatedProducts_returns200_whenDefaults() throws Exception {
        Product a = new Product("A", "Amazon", 10.0F, LocalDate.now().plusMonths(1L), 10);
        Product b = new Product("B", "Bamazon", 10.0F, LocalDate.now().plusMonths(1L), 10);
        a.setId(1L);
        b.setId(2L);
        List<Product> products = List.of(a, b);
        Mockito.when(this.productService.getProductFilteredAndPaginated(1, "id", "", "asc")).thenReturn(products);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products", new Object[0]).accept(new MediaType[]{MediaType.APPLICATION_JSON})).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2))).andExpect(MockMvcResultMatchers.jsonPath("$[0].name", new Object[0]).value("A")).andExpect(MockMvcResultMatchers.jsonPath("$[1].name", new Object[0]).value("B")).andExpect(MockMvcResultMatchers.jsonPath("$[0].category", new Object[0]).value("Amazon")).andExpect(MockMvcResultMatchers.jsonPath("$[1].stock", new Object[0]).value("10"));
    }

    @Test
    void deleteProduct_returns204_becauseDeletingExistingProduct() throws Exception {
        long pathId = 1L;
        Mockito.when(this.productService.deleteProductById(pathId)).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/{id}", new Object[]{pathId})).andExpect(MockMvcResultMatchers.status().isNoContent()).andExpect(MockMvcResultMatchers.content().string(""));
    }

    @Test
    void deleteProduct_returns404_becauseDeletingMissingProduct() throws Exception {
        long pathId = 1L;
        Mockito.when(this.productService.deleteProductById(pathId)).thenReturn(false);
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/{id}", new Object[]{pathId})).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deleteProduct_returns400_becauseDeletingMissingProduct() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/{id}", new Object[]{"abc"})).andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(new Object[]{this.productService});
    }

    @Test
    void deleteProduct_returns400_becauseNegativeId() throws Exception {
        long id = -1L;
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/{id}", new Object[]{id})).andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(new Object[]{this.productService});
    }

    @Test
    void deleteProduct_returns404_afterDeletingTwiceSameProduct() throws Exception {
        long pathId = 1L;
        Mockito.when(this.productService.deleteProductById(pathId)).thenReturn(true, new Boolean[]{false});
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/{id}", new Object[]{pathId})).andExpect(MockMvcResultMatchers.status().isNoContent()).andExpect(MockMvcResultMatchers.content().string(""));
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/{id}", new Object[]{pathId})).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void putProduct_returns200_becauseGoodPut() throws Exception {
        long pathId = 1L;
        Product incoming = new Product("AZ-900", "Certificación AWS", 100.0F, LocalDate.of(2026, 6, 30), 1);
        incoming.setId(1L);
        Product updated = new Product("AZ-900", "Certificación AWS", 100.0F, LocalDate.of(2026, 6, 30), 1);
        updated.setId(1L);
        Mockito.when(this.productService.updateProduct((Product)ArgumentMatchers.any(Product.class))).thenReturn(updated);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/{id}", new Object[]{1L}).contentType(MediaType.APPLICATION_JSON).content(this.objectMapper.writeValueAsString(incoming))).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void putProduct_returns200_becauseOverridesIdFromPath() throws Exception {
        long pathId = 1L;
        Product incoming = new Product("AZ-900", "Certificación AWS", 100.0F, LocalDate.of(2026, 6, 30), 1);
        incoming.setId(2L);
        Product updated = new Product("AZ-900", "Certificación AWS", 100.0F, LocalDate.of(2026, 6, 30), 1);
        updated.setId(1L);
        Mockito.when(this.productService.updateProduct((Product)ArgumentMatchers.any(Product.class))).thenReturn(updated);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/{id}", new Object[]{1L}).contentType(MediaType.APPLICATION_JSON).content(this.objectMapper.writeValueAsString(incoming))).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.id", new Object[0]).value(1));
    }

    @Test
    void putProduct_returns200_becauseLackOfExpirationDate() throws Exception {
        long pathId = 1L;
        Product incoming = new Product("AZ-900", "Certificación AWS", 100.0F, 1);
        incoming.setId(1L);
        Product updated = new Product("AZ-900", "Certificación AWS", 100.0F, 1);
        updated.setId(1L);
        Mockito.when(this.productService.updateProduct((Product)ArgumentMatchers.any(Product.class))).thenReturn(updated);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/{id}", new Object[]{1L}).contentType(MediaType.APPLICATION_JSON).content(this.objectMapper.writeValueAsString(incoming))).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void putProduct_returns200_becauseOfSizeOneName() throws Exception {
        long pathId = 1L;
        Product incoming = new Product("A", "Certificación AWS", 100.0F, 1);
        incoming.setId(1L);
        Product updated = new Product("A", "Certificación AWS", 100.0F, 1);
        updated.setId(1L);
        Mockito.when(this.productService.updateProduct((Product)ArgumentMatchers.any(Product.class))).thenReturn(updated);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/{id}", new Object[]{1L}).contentType(MediaType.APPLICATION_JSON).content(this.objectMapper.writeValueAsString(incoming))).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void putProduct_returns400_becauseOfBlankName() throws Exception {
        long pathId = 1L;
        Product incoming = new Product("", "Certificación AWS", 100.0F, 1);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/{id}", new Object[]{1L}).contentType(MediaType.APPLICATION_JSON).content(this.objectMapper.writeValueAsString(incoming))).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void putProduct_returns400_becauseNoName() throws Exception {
        long pathId = 1L;
        String incoming = "  {\n    \"unitPrice\": 100.0,\n    \"expirationDate\": \"2026-06-30\",\n    \"stock\": 1\n  }\n";
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/{id}", new Object[]{1L}).contentType(MediaType.APPLICATION_JSON).content(incoming)).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void putProduct_returns400_becauseOfPastExpirationDate() throws Exception {
        long pathId = 1L;
        Product incoming = new Product("Certification", "Certificación AWS", 100.0F, LocalDate.now().minusDays(1L), 1);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/{id}", new Object[]{1L}).contentType(MediaType.APPLICATION_JSON).content(this.objectMapper.writeValueAsString(incoming))).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void putProduct_returns400_becauseOfNegativeStock() throws Exception {
        long pathId = 1L;
        Product incoming = new Product("Certification", "Certificación AWS", 100.0F, LocalDate.now(), -1);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/{id}", new Object[]{1L}).contentType(MediaType.APPLICATION_JSON).content(this.objectMapper.writeValueAsString(incoming))).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void putProduct_returns400_becauseOfNegativeUnitPrice() throws Exception {
        long pathId = 1L;
        Product incoming = new Product("Certification", "Certificación AWS", -100.0F, LocalDate.now(), 1);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/{id}", new Object[]{1L}).contentType(MediaType.APPLICATION_JSON).content(this.objectMapper.writeValueAsString(incoming))).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void putProduct_returns400_becauseNameExceedingCharacterLimit() throws Exception {
        long pathId = 1L;
        Product incoming = new Product("This is a deliberately long string created to fulfill the request of being more than 120 characters in length. It contains a variety of words and spaces, ensuring it easily surpasses the specified character count. The purpose of this string is purely demonstrative, illustrating the ability to generate text of a significant length as requested by the user", "Certificación AWS", 100.0F, LocalDate.now(), 1);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/{id}", new Object[]{1L}).contentType(MediaType.APPLICATION_JSON).content(this.objectMapper.writeValueAsString(incoming))).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void saveProduct_returns201_becauseGoodProduct() throws Exception {
        Product incoming = new Product("AWS Cloud Practitioner (CLF-C02) - Exam Voucher", "Certificación AWS", 100.0F, LocalDate.of(2026, 6, 30), 1);
        Product created = new Product("AWS Cloud Practitioner (CLF-C02) - Exam Voucher", "Certificación AWS", 100.0F, LocalDate.of(2026, 6, 30), 1);
        created.setId(1L);
        Mockito.when(this.productService.saveProduct((Product)ArgumentMatchers.any(Product.class))).thenReturn(created);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products", new Object[0]).contentType(MediaType.APPLICATION_JSON).content(this.objectMapper.writeValueAsString(incoming))).andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void saveProduct_returns201_evenWithoutExpirationDate() throws Exception {
        Product incoming = new Product("AWS Cloud Practitioner (CLF-C02) - Exam Voucher", "Certificación AWS", 100.0F, 1);
        Product created = new Product("AWS Cloud Practitioner (CLF-C02) - Exam Voucher", "Certificación AWS", 100.0F, 1);
        created.setId(1L);
        Mockito.when(this.productService.saveProduct((Product)ArgumentMatchers.any(Product.class))).thenReturn(created);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products", new Object[0]).contentType(MediaType.APPLICATION_JSON).content(this.objectMapper.writeValueAsString(incoming))).andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void saveProduct_returns400_becauseNegativePrice() throws Exception {
        Product product = new Product("AWS Cloud Practitioner (CLF-C02) - Exam Voucher", "Certificación AWS", -100.0F, LocalDate.of(2026, 6, 30), 0);
        String body = this.objectMapper.writeValueAsString(product);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products", new Object[0]).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void saveProduct_returns400_becausePastExpirationDate() throws Exception {
        Product product = new Product("AWS Cloud Practitioner (CLF-C02) - Exam Voucher", "Certificación AWS", 100.0F, LocalDate.of(2024, 6, 30), 0);
        String body = this.objectMapper.writeValueAsString(product);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products", new Object[0]).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void saveProduct_returns400_becausePastExpirationDateByOneDay() throws Exception {
        Product product = new Product("AWS Cloud Practitioner (CLF-C02) - Exam Voucher", "Certificación AWS", 100.0F, LocalDate.now().minusDays(1L), 0);
        String body = this.objectMapper.writeValueAsString(product);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products", new Object[0]).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void saveProduct_returns400_becauseNegativeStock() throws Exception {
        Product product = new Product("AWS Cloud Practitioner (CLF-C02) - Exam Voucher", "Certificación AWS", 100.0F, LocalDate.of(2026, 6, 30), -1);
        String body = this.objectMapper.writeValueAsString(product);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products", new Object[0]).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void saveProduct_returns400_becauseNameExceedingCharacterLimit() throws Exception {
        Product product = new Product("This is a deliberately long string created to fulfill the request of being more than 120 characters in length. It contains a variety of words and spaces, ensuring it easily surpasses the specified character count. The purpose of this string is purely demonstrative, illustrating the ability to generate text of a significant length as requested by the user", "Certificación AWS", 100.0F, LocalDate.of(2026, 6, 30), 12);
        String body = this.objectMapper.writeValueAsString(product);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products", new Object[0]).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void saveProduct_returns400_becauseNoName() throws Exception {
        String body = "  {\n    \"category\": \"Certificación AWS\",\n    \"unitPrice\": 100.0,\n    \"expirationDate\": \"2026-06-30\",\n    \"stock\": 1\n  }\n";
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products", new Object[0]).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void saveProduct_returns400_becauseBlankName() throws Exception {
        Product product = new Product("", "Certificación AWS", 100.0F, LocalDate.of(2026, 6, 30), 1);
        String body = this.objectMapper.writeValueAsString(product);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products", new Object[0]).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void saveProduct_returns400_becauseBlankCategory() throws Exception {
        Product product = new Product("AWS CERTIFICATION", "", 100.0F, LocalDate.of(2026, 6, 30), 1);
        String body = this.objectMapper.writeValueAsString(product);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products", new Object[0]).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void saveProduct_returns400_becauseNoCategory() throws Exception {
        String body = "  {\n    \"name\": \"AWS CERTIFICATION\",\n    \"unitPrice\": 100.0,\n    \"expirationDate\": \"2026-06-30\",\n    \"stock\": 1\n  }\n";
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products", new Object[0]).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}