
package mike.sparkd.back_end_inventory_manager.product;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getProductFilteredAndPaginated(int page, String filter, String filterTwo, String direction) {
        return this.productRepository.getFilteredAndPaginatedProducts(filter, filterTwo, page, direction);
    }

    public Product saveProduct(@Valid Product product) {
        return this.productRepository.save(product);
    }

    public Product updateProduct(@Valid Product product) {
        return this.productRepository.update(product);
    }

    public boolean deleteProductById(long id) {
        return this.productRepository.deleteById(id);
    }

    public List<CategoryInventorySummary> getAllCategoryMetrics(){
        return this.productRepository.getInventorySummaryByCategory();
    }
}