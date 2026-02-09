package mike.sparkd.back_end_inventory_manager.product;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import mike.sparkd.back_end_inventory_manager.common.exception.BadRequestException;
import mike.sparkd.back_end_inventory_manager.common.exception.NotFoundException;
import mike.sparkd.back_end_inventory_manager.product.Model.CategoryInventorySummary;
import mike.sparkd.back_end_inventory_manager.product.Model.PageResponse;
import mike.sparkd.back_end_inventory_manager.product.Model.Product;
import mike.sparkd.back_end_inventory_manager.product.Repository.InMemoryProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InMemoryProductRepositoryTest {

    private InMemoryProductRepository repository;

    @BeforeEach
    void setUpRepository() {
        this.repository = new InMemoryProductRepository();
    }

    // ---------- save / getProductById / getAllProducts ----------

    @Test
    void save_assignsIdAndDates() {
        int sizeBefore = repository.getSize();

        Product p = new Product("Test", "Cat", 10.0F, LocalDate.now().plusDays(1), 5);
        Product saved = repository.save(p);

        Assertions.assertNotNull(saved.getId());
        Assertions.assertEquals(sizeBefore + 1, repository.getSize());
        Assertions.assertNotNull(saved.getCreationDate());
        Assertions.assertNotNull(saved.getUpdateDate());

        Product reloaded = repository.getProductById(saved.getId())
                .orElseThrow(() -> new NoSuchElementException("Not found"));
        Assertions.assertEquals("Test", reloaded.getName());
    }

    @Test
    void getAllProducts_returnsSameSizeAsInternalMap() {
        List<Product> all = repository.getAllProducts();
        Assertions.assertEquals(repository.getSize(), all.size());
    }

    // ---------- update ----------

    @Test
    void updateProduct_works() {
        Random random = new Random();
        long lowerBound = 1L;
        long upperBound = repository.getSize();
        long id = lowerBound + (long) (random.nextDouble() * (upperBound - lowerBound + 1L));

        Product original = repository.getProductById(id)
                .orElseThrow(() -> new NoSuchElementException("Product " + id + " not found"));

        String oldName = original.getName();

        Product toUpdate = new Product(
                original.getName(),
                original.getCategory(),
                original.getUnitPrice(),
                original.getExpirationDate(),
                original.getStock()
        );
        toUpdate.setId(id);
        toUpdate.setName("Copy");

        Product updated = repository.update(toUpdate);

        Assertions.assertEquals(id, updated.getId());
        Assertions.assertEquals("Copy", updated.getName());
        Assertions.assertNotEquals(oldName, updated.getName());

        Product reloaded = repository.getProductById(id).orElseThrow();
        Assertions.assertEquals("Copy", reloaded.getName());
        // creationDate debe mantenerse
        Assertions.assertEquals(original.getCreationDate(), reloaded.getCreationDate());
    }

    @Test
    void updateProduct_throwsNotFoundWhenMissing() {
        int sizeBefore = repository.getSize();
        long missingId = 999L;

        Product ghost = new Product("Does not exist", "None", 1.0F, LocalDate.now().plusDays(1), 1);
        ghost.setId(missingId);

        Assertions.assertThrows(NotFoundException.class, () -> repository.update(ghost));
        Assertions.assertEquals(sizeBefore, repository.getSize());
    }

    // ---------- deleteById ----------

    @Test
    void deleteProductById_deletesExistingProduct() {
        long existingId = 1L;
        Assertions.assertTrue(repository.getProductById(existingId).isPresent());

        boolean deleted = repository.deleteById(existingId);
        Assertions.assertTrue(deleted);
        Assertions.assertTrue(repository.getProductById(existingId).isEmpty());
    }

    @Test
    void deleteProductById_returnsFalseWhenMissing() {
        boolean deleted = repository.deleteById(999L);
        Assertions.assertFalse(deleted);
    }

    // ---------- getFilteredAndPaginatedProducts (usa paginate) ----------

    @Test
    void getFilteredAndPaginatedProducts_pageZero_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> repository.getFilteredAndPaginatedProducts("id", "", 0, "asc"));
    }

    @Test
    void getFilteredAndPaginatedProducts_sortByIdAsc_andPagination() {
        List<Product> page1 = repository.getFilteredAndPaginatedProducts("id", "id", 1, "asc");
        Assertions.assertEquals(10, page1.size());
        Assertions.assertEquals(1L, page1.get(0).getId());

        List<Product> page2 = repository.getFilteredAndPaginatedProducts("id", "id", 2, "asc");
        Assertions.assertEquals(10, page2.size());
        Assertions.assertEquals(11L, page2.get(0).getId());
        Assertions.assertEquals(20L, page2.get(9).getId());

        Assertions.assertTrue(repository.getFilteredAndPaginatedProducts("id", "id", 999, "asc").isEmpty());
    }

    @Test
    void getFilteredAndPaginatedProducts_sortByIdDesc() {
        // page 1: ids deberían venir en orden descendente
        List<Product> page1 = repository.getFilteredAndPaginatedProducts("id", "id", 1, "desc");
        Assertions.assertEquals(10, page1.size());

        for (int i = 1; i < page1.size(); i++) {
            long prev = page1.get(i - 1).getId();
            long curr = page1.get(i).getId();
            Assertions.assertTrue(prev >= curr, "lista no está ordenada desc por id en page1");
        }

        // page 2: sigue descendente respecto a page1
        List<Product> page2 = repository.getFilteredAndPaginatedProducts("id", "id", 2, "desc");
        Assertions.assertEquals(10, page2.size());

        // el primer elemento de page2 debe ser <= último de page1
        Assertions.assertTrue(
                page1.get(page1.size() - 1).getId() >= page2.get(0).getId()
        );

        for (int i = 1; i < page2.size(); i++) {
            long prev = page2.get(i - 1).getId();
            long curr = page2.get(i).getId();
            Assertions.assertTrue(prev >= curr, "lista no está ordenada desc por id en page2");
        }
    }

    @Test
    void getFilteredAndPaginatedProducts_twoDifferentFilters_stockThenName() {
        List<Product> page1 = repository.getFilteredAndPaginatedProducts("stock", "name", 1, "asc");
        Assertions.assertEquals(10, page1.size());

        // Llamamos de nuevo con los mismos parámetros y validamos que el resultado sea consistente
        List<Product> page2 = repository.getFilteredAndPaginatedProducts("stock", "name", 1, "asc");
        Assertions.assertEquals(10, page2.size());
        Assertions.assertEquals(page1, page2);
    }


    // ---------- getByParamsSearch (filtros, sort, paginación) ----------

    @Test
    void getByParamsSearch_filtersByNameCategoryAndAvailabilityIn() {
        PageResponse<Product> page = repository.getByParamsSearch(
                "AWS",
                "Certificación Cloud",
                "in",
                1,
                20,
                "name",
                "asc"
        );

        List<Product> content = page.getContent();
        Assertions.assertFalse(content.isEmpty());

        for (Product p : content) {
            Assertions.assertTrue(p.getName().toLowerCase().contains("aws"));
            Assertions.assertEquals("Certificación Cloud", p.getCategory());
            Assertions.assertTrue(p.getStock() > 0);
        }

        for (int i = 1; i < content.size(); i++) {
            String prev = content.get(i - 1).getName().toLowerCase();
            String curr = content.get(i).getName().toLowerCase();
            Assertions.assertTrue(prev.compareTo(curr) <= 0);
        }
    }

    @Test
    void getByParamsSearch_paginatesCorrectly_allAvailability() {
        PageResponse<Product> page1 = repository.getByParamsSearch(
                null, null, "all",
                1, 10,
                "id", "asc"
        );
        PageResponse<Product> page2 = repository.getByParamsSearch(
                null, null, "all",
                2, 10,
                "id", "asc"
        );

        Assertions.assertEquals(10, page1.getContent().size());
        Assertions.assertEquals(10, page2.getContent().size());
        Assertions.assertEquals(1L, page1.getContent().get(0).getId());
        Assertions.assertEquals(11L, page2.getContent().get(0).getId());
    }

    @Test
    void getByParamsSearch_pageZero_throwsBadRequestException() {
        Assertions.assertThrows(BadRequestException.class, () ->
                repository.getByParamsSearch(null, null, "all", 0, 10, "id", "asc")
        );
    }

    @Test
    void getByParamsSearch_sizeZero_defaultsToTen() {
        PageResponse<Product> page = repository.getByParamsSearch(
                null, null, "all",
                1, 0,      // size = 0 -> debe ajustarse a 10
                "id", "asc"
        );

        Assertions.assertEquals(10, page.getSize());
        Assertions.assertEquals(10, page.getContent().size());
    }

    @Test
    void getByParamsSearch_availabilityVariants_in_out_all_unknown_null() {
        PageResponse<Product> all = repository.getByParamsSearch(null, null, "all", 1, 100, "id", "asc");
        PageResponse<Product> in = repository.getByParamsSearch(null, null, "in", 1, 100, "id", "asc");
        PageResponse<Product> out = repository.getByParamsSearch(null, null, "out", 1, 100, "id", "asc");
        PageResponse<Product> availabilityNull = repository.getByParamsSearch(null, null, null, 1, 100, "id", "asc");
        PageResponse<Product> availabilityUnknown = repository.getByParamsSearch(null, null, "xyz", 1, 100, "id", "asc");

        // null y "xyz" deben comportarse igual que "all" (sin filtro)
        Assertions.assertEquals(all.getTotalElements(), availabilityNull.getTotalElements());
        Assertions.assertEquals(all.getTotalElements(), availabilityUnknown.getTotalElements());

        for (Product p : in.getContent()) {
            Assertions.assertTrue(p.getStock() > 0);
        }
        for (Product p : out.getContent()) {
            Assertions.assertTrue(p.getStock() <= 0);
        }
    }

    @Test
    void getByParamsSearch_sortByUnitPriceDesc() {
        PageResponse<Product> page = repository.getByParamsSearch(
                null, null, "all",
                1, 100,
                "unitPrice", "desc"
        );
        List<Product> content = page.getContent();

        for (int i = 1; i < content.size(); i++) {
            float prev = content.get(i - 1).getUnitPrice();
            float curr = content.get(i).getUnitPrice();
            Assertions.assertTrue(prev >= curr);
        }
    }

    @Test
    void getByParamsSearch_sortByExpirationDateAsc_nullsLast() {
        PageResponse<Product> page = repository.getByParamsSearch(
                null, null, "all",
                1, 100,
                "expirationDate", "asc"
        );
        List<Product> content = page.getContent();

        boolean seenNull = false;
        for (Product p : content) {
            if (p.getExpirationDate() == null) {
                seenNull = true;
            } else {
                // una vez que vimos null, no debería volver a salir una fecha no nula
                Assertions.assertFalse(seenNull);
            }
        }
    }

    // ---------- getInventorySummaryByCategory ----------

    @Test
    void getInventorySummaryByCategory_includesNoCategoryAndRoundsAverage() {
        // producto sin categoría -> NO-CATEGORY
        Product noCat = new Product("Sin categoría", "", 10.0F, null, 3);
        repository.save(noCat);

        // producto con misma categoría para probar acumulado y redondeo
        Product noCat2 = new Product("Sin categoría 2", "", 20.0F, null, 1);
        repository.save(noCat2);

        List<CategoryInventorySummary> summaries = repository.getInventorySummaryByCategory();

        CategoryInventorySummary noCategorySummary = summaries.stream()
                .filter(s -> "NO-CATEGORY".equals(s.getCategory()))
                .findFirst()
                .orElseThrow();

        int expectedUnits = 3 + 1; // 4
        double expectedTotalValue = (10.0 * 3) + (20.0 * 1); // 50.0
        double expectedAvg = expectedTotalValue / expectedUnits; // 12.5

        Assertions.assertEquals(expectedUnits, noCategorySummary.getTotalUnitsInStock());
        Assertions.assertEquals(expectedTotalValue, noCategorySummary.getTotalStockValue(), 0.001);
        // se redondea a 2 decimales
        Assertions.assertEquals(12.5, noCategorySummary.getAverageUnitPriceInStock(), 0.001);
    }
}
