
package mike.sparkd.back_end_inventory_manager.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        path = {"api/v1/products"}
)
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping
    public PageResponse<Product> getSearchedProducts(
            @RequestParam(name="page", defaultValue="1") int page,
            @RequestParam(name="size", defaultValue="10") int size,
            @RequestParam(name="name", required=false) String name,
            @RequestParam(name="category", required=false) String category,
            @RequestParam(name="availability", required=false, defaultValue="all") String availability, // in|out|all
            @RequestParam(name="sortBy", defaultValue="id") String sortBy, // id|name|unitPrice|stock|expirationDate
            @RequestParam(name="direction", defaultValue="asc") String direction
    ) {
        return productService.search(page, size, name, category, availability, sortBy, direction);
    }

    @GetMapping({"/metrics"})
    public List<CategoryInventorySummary> getInventorySummaryByCategory(){
        return this.productService.getAllCategoryMetrics();
    }

    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody @Valid Product product) {
        Product created = this.productService.saveProduct(product);
        return ResponseEntity.created(URI.create("/api/v1/products/" + created.getId())).body(created);
    }

    @PutMapping({"/{id}"})
    public ResponseEntity<Product> updateProduct(@PathVariable @Positive long id, @RequestBody @Valid Product product) {
        product.setId(id);
        Product updated = this.productService.updateProduct(product);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<Void> deleteProduct(@PathVariable @Positive long id) {
        boolean deleted = this.productService.deleteProductById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
