package mike.sparkd.back_end_inventory_manager.category;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/categories")
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // GET /api/v1/categories
    @GetMapping
    public List<CategoryReadDto> list() {
        return categoryService.getAllCategories().stream()
                .sorted(Comparator.comparing(
                        c -> Optional.ofNullable(c.getName()).orElse(""),
                        String.CASE_INSENSITIVE_ORDER
                ))
                .map(CategoryReadDto::from)
                .toList();
    }

    // GET /api/v1/categories/{id}
    @GetMapping("/{id}")
    public Category getById(@PathVariable @Positive long id) {
        return categoryService.getAllCategories().stream()
                .filter(c -> c.getId() != null && c.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("category " + id + " not found"));
    }

    // POST /api/v1/categories
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category create(@RequestBody @Valid Category category) {
        return categoryService.saveCategory(category);
    }

    // PUT /api/v1/categories/{id}
    @PutMapping("/{id}")
    public Category update(@PathVariable @Positive long id,
                           @RequestBody @Valid Category category) {
        category.setId(id);
        return categoryService.updateCategory(category);
    }

    // DELETE /api/v1/categories/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive long id) {
        boolean deleted = categoryService.deleteCategoryById(id);
        if (!deleted) {
            throw new NoSuchElementException("category " + id + " not found");
        }
    }
}