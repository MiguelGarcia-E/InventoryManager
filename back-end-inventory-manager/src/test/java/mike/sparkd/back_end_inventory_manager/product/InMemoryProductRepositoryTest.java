
package mike.sparkd.back_end_inventory_manager.product;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
public class InMemoryProductRepositoryTest {
    private InMemoryProductRepository repository;

    @BeforeEach
    void setUpRepository() {
        this.repository = new InMemoryProductRepository();
    }

    @Test
    void updateProduct_works() {
        Random random = new Random();
        long lowerBound = 1L;
        long upperBound = (long)this.repository.getSize();
        long id = lowerBound + (long)(random.nextDouble() * (double)(upperBound - lowerBound + 1L));
        Product original = (Product)this.repository.getProductById(id).orElseThrow(() -> new NoSuchElementException("Product " + id + " not found"));
        String oldName = original.getName();
        Product toUpdate = new Product(original.getName(), original.getCategory(), original.getUnitPrice(), original.getExpirationDate(), original.getStock());
        toUpdate.setId(id);
        toUpdate.setName("Copy");
        Product updated = this.repository.update(toUpdate);
        Assertions.assertEquals(id, updated.getId());
        Assertions.assertEquals("Copy", updated.getName());
        Assertions.assertNotEquals(oldName, updated.getName());
        Product reloaded = (Product)this.repository.getProductById(id).orElseThrow();
        Assertions.assertEquals("Copy", reloaded.getName());
    }

    @Test
    void updateProduct_throwsWhenNotFound() {
        int sizeBefore = this.repository.getSize();
        long missingId = 999L;
        Product ghost = new Product("Does not exist", "None", 1.0F, LocalDate.now().plusDays(1L), 1);
        ghost.setId(missingId);
        Assertions.assertThrows(NoSuchElementException.class, () -> this.repository.update(ghost));
        Assertions.assertEquals(sizeBefore, this.repository.getSize());
    }

    @Test
    void deleteProductById_deletesProduct() {
        Random random = new Random();
        long lowerBound = 1L;
        long upperBound = (long)this.repository.getSize();
        long randomBoundedLong = lowerBound + (long)(random.nextDouble() * (double)(upperBound - lowerBound + 1L));
        boolean deleted = this.repository.deleteById(randomBoundedLong);
        Assertions.assertTrue(deleted);
    }

    @Test
    void deleteProductById_cannotDeleteUnexistentProduct() {
        boolean deleted = this.repository.deleteById(999L);
        Assertions.assertFalse(deleted);
    }

    @Test
    void getAllProducts_gets30Products() {
        List<Product> allProducts = this.repository.getAllProducts();
        Assertions.assertEquals(allProducts.size(), this.repository.getSize());
    }

    @Test
    void getFilteredAndPaginatedProducts_throwsIllegalArgumentException_becausePageZero() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.repository.getFilteredAndPaginatedProducts("id", "", 0, "asc"));
    }

    @Test
    void getFilteredAndPaginatedProducts_pageItemsSize() {
        List<Product> page = this.repository.getFilteredAndPaginatedProducts("id", "id", 1, "asc");
        Assertions.assertEquals(10, page.size());
        Assertions.assertEquals(1L, ((Product)page.get(0)).getId());
        List<Product> page2 = this.repository.getFilteredAndPaginatedProducts("id", "id", 2, "asc");
        Assertions.assertEquals(10, page2.size());
        Assertions.assertEquals(11L, ((Product)page2.get(0)).getId());
        Assertions.assertEquals(20L, ((Product)page2.get(9)).getId());
        Assertions.assertTrue(this.repository.getFilteredAndPaginatedProducts("id", "id", 999, "asc").isEmpty());
    }

    @Test
    void getFilteredAndPaginatedProducts_descOrder() {
        List<Product> page = this.repository.getFilteredAndPaginatedProducts("id", "id", 1, "desc");
        Assertions.assertEquals(10, page.size());
        Assertions.assertEquals(30L, ((Product)page.get(0)).getId());
        page = this.repository.getFilteredAndPaginatedProducts("id", "id", 3, "desc");
        Assertions.assertEquals(10, page.size());
        Assertions.assertEquals(1L, ((Product)page.get(9)).getId());
    }

    @Test
    void getFilteredAndPaginatedProducts_twoDiferentFilters() {
        List<Product> page = this.repository.getFilteredAndPaginatedProducts("stock", "name", 1, "asc");
        Assertions.assertEquals(10, page.size());

        for(int i = 1; i < page.size(); ++i) {
            int prev = ((Product)page.get(i - 1)).getStock();
            int curr = ((Product)page.get(i)).getStock();
            Assertions.assertTrue(prev <= curr);
            if (prev == curr) {
                String prevName = ((Product)page.get(i - 1)).getName().toLowerCase();
                String currName = ((Product)page.get(i)).getName().toLowerCase();
                Assertions.assertTrue(prevName.compareTo(currName) <= 0);
            }
        }

    }

    @Test
    void getFilteredAndPaginatedProducts_filterByStock() {
        List<Product> page = this.repository.getFilteredAndPaginatedProducts("stock", "id", 1, "asc");
        Assertions.assertEquals(10, page.size());

        for(int i = 1; i < page.size(); ++i) {
            int prev = ((Product)page.get(i - 1)).getStock();
            int curr = ((Product)page.get(i)).getStock();
            Assertions.assertTrue(prev <= curr);
        }

    }

    @Test
    void getFilteredAndPaginatedProducts_filterByUnitPrice() {
        List<Product> page = this.repository.getFilteredAndPaginatedProducts("unitPrice", "id", 1, "asc");
        Assertions.assertEquals(10, page.size());

        for(int i = 1; i < page.size(); ++i) {
            float prev = ((Product)page.get(i - 1)).getUnitPrice();
            float curr = ((Product)page.get(i)).getUnitPrice();
            Assertions.assertTrue(prev <= curr);
        }

    }

    @Test
    void getFilteredAndPaginatedProducts_filterByExpirationDate() {
        List<Product> page = this.repository.getFilteredAndPaginatedProducts("expirationDate", "id", 1, "asc");
        Assertions.assertEquals(10, page.size());

        for(int i = 1; i < page.size(); ++i) {
            LocalDate prev = ((Product)page.get(i - 1)).getExpirationDate();
            LocalDate curr = ((Product)page.get(i)).getExpirationDate();
            Assertions.assertTrue(prev.isBefore(curr) || prev.isEqual(curr));
        }

    }

    @Test
    void getFilteredAndPaginatedProducts_filterByName() {
        List<Product> page = this.repository.getFilteredAndPaginatedProducts("name", "id", 1, "asc");
        Assertions.assertEquals(10, page.size());

        for(int i = 1; i < page.size(); ++i) {
            String prev = ((Product)page.get(i - 1)).getName();
            String curr = ((Product)page.get(i)).getName();
            Assertions.assertTrue(prev.toLowerCase().compareTo(curr.toLowerCase()) <= 0);
        }

    }

    @Test
    void getFilteredAndPaginatedProducts_filterByCategory() {
        List<Product> page = this.repository.getFilteredAndPaginatedProducts("category", "id", 1, "asc");
        Assertions.assertEquals(10, page.size());

        for(int i = 1; i < page.size(); ++i) {
            String prev = ((Product)page.get(i - 1)).getCategory();
            String curr = ((Product)page.get(i)).getCategory();
            Assertions.assertTrue(prev.toLowerCase().compareTo(curr.toLowerCase()) <= 0);
        }

    }

    @Test
    void getFilteredAndPaginatedProducts_filterByBlankFilters() {
        List<Product> page = this.repository.getFilteredAndPaginatedProducts("", "", 1, "asc");
        Assertions.assertEquals(10, page.size());

        for(int i = 1; i < page.size(); ++i) {
            long prev = ((Product)page.get(i - 1)).getId();
            long curr = ((Product)page.get(i)).getId();
            Assertions.assertTrue(prev <= curr);
        }

    }
}
