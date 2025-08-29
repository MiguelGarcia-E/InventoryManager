package mike.sparkd.back_end_inventory_manager.product;


import mike.sparkd.back_end_inventory_manager.category.CategoryRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> getFilteredAndPaginatedProducts(String filters, String filterTwo, int page, String direction);

    List<Product> getAllProducts();

    Optional<Product> getProductById(long id);

    Product save(Product product);

    Product update(Product product);

    boolean deleteById(long id);

    List<CategoryInventorySummary> getInventorySummaryByCategory();

    PageResponse<Product> getByParamsSearch(            String name,
                                                        String category,
                                                        String availability,
                                                        int page,
                                                        int size,
                                                        String sortBy,
                                                        String direction );

}