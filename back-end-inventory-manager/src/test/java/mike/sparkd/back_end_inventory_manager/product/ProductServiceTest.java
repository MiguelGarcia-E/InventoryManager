package mike.sparkd.back_end_inventory_manager.product;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
public class ProductServiceTest {
    private final ProductRepository repository = (ProductRepository)Mockito.mock(ProductRepository.class);
    private final ProductService service;

    public ProductServiceTest() {
        this.service = new ProductService(this.repository);
    }

    private static Product p(String name, String category, float unitprice, LocalDate expirationDate, int stock) {
        return new Product(name, category, unitprice, expirationDate, stock);
    }

    @Test
    void getProductFilteredAndPaginated() {
        Product a = new Product("A", "Amazon", 10.0F, LocalDate.now().plusMonths(1L), 10);
        Product b = new Product("B", "Bamazon", 10.0F, LocalDate.now().plusMonths(1L), 10);
        Product c = new Product("B", "Amazon", 10.0F, LocalDate.now().plusMonths(1L), 10);
        a.setId(1L);
        b.setId(2L);
        c.setId(3L);
        List<Product> expectedProductReturn = List.of(a, c, b);
        Mockito.when(this.repository.getFilteredAndPaginatedProducts("name", "category", 1, "asc")).thenReturn(expectedProductReturn);
        List<Product> outcome = this.service.getProductFilteredAndPaginated(1, "name", "category", "asc");
        Assertions.assertEquals(outcome, expectedProductReturn);
    }

    @Test
    void saveProduct_savesAndRepoReturnsResult() {
        Product incoming = p("Amazon", "Cat", 9.9F, LocalDate.now().plusDays(10L), 3);
        Product outcoming = p("Amazon", "Cat", 9.9F, LocalDate.now().plusDays(10L), 3);
        Mockito.when(this.repository.save(incoming)).thenReturn(outcoming);
        Product saved = this.service.saveProduct(incoming);
        Assertions.assertEquals(saved, outcoming);
        ((ProductRepository)Mockito.verify(this.repository)).save(incoming);
    }

    @Test
    void updateProduct_updatesAndReturnsResult() {
        Product incoming = p("Amazon", "Cat", 9.9F, LocalDate.now().plusDays(10L), 3);
        Product outcoming = p("Amazon", "Cat", 9.9F, LocalDate.now().plusDays(10L), 3);
        Mockito.when(this.repository.update(incoming)).thenReturn(outcoming);
        Product updated = this.service.updateProduct(incoming);
        Assertions.assertEquals(updated, outcoming);
        ((ProductRepository)Mockito.verify(this.repository)).update(incoming);
    }

    @Test
    void deleteProductById_deletesAndReturnsBoolean() {
        Mockito.when(this.repository.deleteById(1L)).thenReturn(true);
        Mockito.when(this.repository.deleteById(2L)).thenReturn(false);
        Assertions.assertTrue(this.service.deleteProductById(1L));
        Assertions.assertFalse(this.service.deleteProductById(2L));
        ((ProductRepository)Mockito.verify(this.repository)).deleteById(1L);
        ((ProductRepository)Mockito.verify(this.repository)).deleteById(2L);
    }
}
