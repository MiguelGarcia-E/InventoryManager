package mike.sparkd.back_end_inventory_manager.category;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/categories")
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // GET /api/v1/categories?page=1&size=10&sort=id&direction=asc
    @GetMapping
    public List<Category> list(
            @RequestParam(defaultValue = "1") @Positive int page,
            @RequestParam(defaultValue = "10") @Positive int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        List<Category> all = categoryService.getAllCategories();

        Comparator<Category> cmp = switch (sort) {
            case "name" -> Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(Category::getId);
        };

        if ("desc".equalsIgnoreCase(direction)) {
            cmp = cmp.reversed();
        }

        List<Category> sorted = all.stream().sorted(cmp).toList();

        int from = (page - 1) * size;
        int to = Math.min(from + size, sorted.size());

        return (from >= sorted.size()) ? List.of() : sorted.subList(from, to);
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
        category.setId(id); // aseguramos que el id del path mande
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