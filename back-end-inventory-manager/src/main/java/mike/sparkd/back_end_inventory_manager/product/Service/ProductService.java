
package mike.sparkd.back_end_inventory_manager.product.Service;

import jakarta.validation.Valid;
import java.util.List;

import mike.sparkd.back_end_inventory_manager.product.Model.CategoryInventorySummary;
import mike.sparkd.back_end_inventory_manager.product.Model.PageResponse;
import mike.sparkd.back_end_inventory_manager.product.Model.Product;
import mike.sparkd.back_end_inventory_manager.product.Repository.ProductRepository;
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

    public PageResponse<Product> search(
            Integer page, Integer size,
            String name, String category, String availability,
            String sortBy, String direction
    ) {
        int p = (page == null || page < 1) ? 1 : page;
        int s = (size == null || size < 1) ? 10 : size;
        String a = (availability == null || availability.isBlank()) ? "all" : availability;
        String sb = (sortBy == null || sortBy.isBlank()) ? "id" : sortBy;
        String dir = "desc".equalsIgnoreCase(direction) ? "desc" : "asc";
        return productRepository.getByParamsSearch(name, category, a, p, s, sb, dir);
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