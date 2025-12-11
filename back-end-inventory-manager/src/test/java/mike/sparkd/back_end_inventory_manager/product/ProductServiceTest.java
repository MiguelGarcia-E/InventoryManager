package mike.sparkd.back_end_inventory_manager.product;

import java.time.LocalDate;
import java.util.List;

import mike.sparkd.back_end_inventory_manager.product.Model.CategoryInventorySummary;
import mike.sparkd.back_end_inventory_manager.product.Model.PageResponse;
import mike.sparkd.back_end_inventory_manager.product.Model.Product;
import mike.sparkd.back_end_inventory_manager.product.Repository.ProductRepository;
import mike.sparkd.back_end_inventory_manager.product.Service.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    private final ProductRepository repository = Mockito.mock(ProductRepository.class);
    private final ProductService service;

    public ProductServiceTest() {
        this.service = new ProductService(this.repository);
    }

    private static Product p(String name, String category, float unitprice, LocalDate expirationDate, int stock) {
        return new Product(name, category, unitprice, expirationDate, stock);
    }

    // -------- getProductFilteredAndPaginated --------

    @Test
    void getProductFilteredAndPaginated_delegatesToRepository() {
        Product a = new Product("A", "Amazon", 10.0F, LocalDate.now().plusMonths(1L), 10);
        Product b = new Product("B", "Bamazon", 10.0F, LocalDate.now().plusMonths(1L), 10);
        a.setId(1L);
        b.setId(2L);

        List<Product> expected = List.of(a, b);

        Mockito.when(repository.getFilteredAndPaginatedProducts("name", "category", 2, "desc"))
                .thenReturn(expected);

        List<Product> result = service.getProductFilteredAndPaginated(2, "name", "category", "desc");

        Assertions.assertEquals(expected, result);
        Mockito.verify(repository).getFilteredAndPaginatedProducts("name", "category", 2, "desc");
    }

    // -------- save / update / delete --------

    @Test
    void saveProduct_savesAndRepoReturnsResult() {
        Product incoming = p("Amazon", "Cat", 9.9F, LocalDate.now().plusDays(10L), 3);
        Product outcoming = p("Amazon", "Cat", 9.9F, LocalDate.now().plusDays(10L), 3);

        Mockito.when(repository.save(incoming)).thenReturn(outcoming);

        Product saved = service.saveProduct(incoming);

        Assertions.assertEquals(outcoming, saved);
        Mockito.verify(repository).save(incoming);
    }

    @Test
    void updateProduct_updatesAndReturnsResult() {
        Product incoming = p("Amazon", "Cat", 9.9F, LocalDate.now().plusDays(10L), 3);
        Product outcoming = p("Amazon", "Cat", 9.9F, LocalDate.now().plusDays(10L), 3);

        Mockito.when(repository.update(incoming)).thenReturn(outcoming);

        Product updated = service.updateProduct(incoming);

        Assertions.assertEquals(outcoming, updated);
        Mockito.verify(repository).update(incoming);
    }

    @Test
    void deleteProductById_deletesAndReturnsBoolean() {
        Mockito.when(repository.deleteById(1L)).thenReturn(true);
        Mockito.when(repository.deleteById(2L)).thenReturn(false);

        Assertions.assertTrue(service.deleteProductById(1L));
        Assertions.assertFalse(service.deleteProductById(2L));

        Mockito.verify(repository).deleteById(1L);
        Mockito.verify(repository).deleteById(2L);
    }

    // -------- search: normalización de parámetros --------

    @Test
    void search_defaultsParametersWhenAllNull() {
        PageResponse<Product> expected = new PageResponse<>(List.of(), 1, 10, 0L);

        Mockito.when(repository.getByParamsSearch(
                        ArgumentMatchers.isNull(), // name
                        ArgumentMatchers.isNull(), // category
                        ArgumentMatchers.eq("all"),
                        ArgumentMatchers.eq(1),
                        ArgumentMatchers.eq(10),
                        ArgumentMatchers.eq("id"),
                        ArgumentMatchers.eq("asc")
                ))
                .thenReturn(expected);

        PageResponse<Product> result = service.search(
                null,  // page
                null,  // size
                null,  // name
                null,  // category
                null,  // availability
                null,  // sortBy
                null   // direction
        );

        Assertions.assertSame(expected, result);

        Mockito.verify(repository).getByParamsSearch(
                null,
                null,
                "all",
                1,
                10,
                "id",
                "asc"
        );
    }

    @Test
    void search_respectsExplicitParametersAndDescDirection() {
        PageResponse<Product> expected = new PageResponse<>(List.of(), 2, 5, 0L);

        Mockito.when(repository.getByParamsSearch(
                        ArgumentMatchers.eq("AWS"),
                        ArgumentMatchers.eq("Cloud"),
                        ArgumentMatchers.eq("in"),
                        ArgumentMatchers.eq(2),
                        ArgumentMatchers.eq(5),
                        ArgumentMatchers.eq("name"),
                        ArgumentMatchers.eq("desc")
                ))
                .thenReturn(expected);

        PageResponse<Product> result = service.search(
                2,      // page
                5,      // size
                "AWS",  // name
                "Cloud",// category
                "in",   // availability
                "name", // sortBy
                "desc"  // direction
        );

        Assertions.assertSame(expected, result);

        Mockito.verify(repository).getByParamsSearch(
                "AWS",
                "Cloud",
                "in",
                2,
                5,
                "name",
                "desc"
        );
    }

    @Test
    void search_normalizesNegativePageAndSize() {
        PageResponse<Product> expected = new PageResponse<>(List.of(), 1, 10, 0L);

        Mockito.when(repository.getByParamsSearch(
                        ArgumentMatchers.isNull(),
                        ArgumentMatchers.isNull(),
                        ArgumentMatchers.eq("all"),
                        ArgumentMatchers.eq(1),   // page < 1 -> 1
                        ArgumentMatchers.eq(10),  // size < 1 -> 10
                        ArgumentMatchers.eq("id"),
                        ArgumentMatchers.eq("asc")
                ))
                .thenReturn(expected);

        PageResponse<Product> result = service.search(
                0,      // page -> debe forzarse a 1
                -5,     // size -> debe forzarse a 10
                null,
                null,
                null,
                null,
                "whatever" // != "desc" => "asc"
        );

        Assertions.assertSame(expected, result);

        Mockito.verify(repository).getByParamsSearch(
                null,
                null,
                "all",
                1,
                10,
                "id",
                "asc"
        );
    }

    @Test
    void search_normalizesBlankAvailabilityAndSortBy_andDirectionNotDesc() {
        PageResponse<Product> expected = new PageResponse<>(List.of(), 3, 20, 0L);

        Mockito.when(repository.getByParamsSearch(
                        ArgumentMatchers.eq("GCP"),
                        ArgumentMatchers.eq("Cloud"),
                        ArgumentMatchers.eq("all"), // availability blank -> "all"
                        ArgumentMatchers.eq(3),      // page ok
                        ArgumentMatchers.eq(20),     // size ok
                        ArgumentMatchers.eq("id"),   // sortBy blank -> "id"
                        ArgumentMatchers.eq("asc")   // direction != "desc" -> "asc"
                ))
                .thenReturn(expected);

        PageResponse<Product> result = service.search(
                3,          // page
                20,         // size
                "GCP",      // name
                "Cloud",    // category
                "   ",      // availability blank
                "   ",      // sortBy blank
                "ASC"       // != "desc" (case-insensitive) => "asc"
        );

        Assertions.assertSame(expected, result);

        Mockito.verify(repository).getByParamsSearch(
                "GCP",
                "Cloud",
                "all",
                3,
                20,
                "id",
                "asc"
        );
    }

    // -------- métricas --------

    @Test
    void getAllCategoryMetrics_delegatesToRepository() {
        List<CategoryInventorySummary> expected = List.of(
                new CategoryInventorySummary("Cloud", 10, 1000.0, 100.0),
                new CategoryInventorySummary("DevOps", 5, 500.0, 100.0)
        );

        Mockito.when(repository.getInventorySummaryByCategory()).thenReturn(expected);

        List<CategoryInventorySummary> result = service.getAllCategoryMetrics();

        Assertions.assertEquals(expected, result);
        Mockito.verify(repository).getInventorySummaryByCategory();
    }
}
