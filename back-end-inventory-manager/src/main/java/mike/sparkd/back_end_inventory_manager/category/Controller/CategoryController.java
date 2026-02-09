package mike.sparkd.back_end_inventory_manager.category.Controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import mike.sparkd.back_end_inventory_manager.category.Service.CategoryService;
import mike.sparkd.back_end_inventory_manager.category.Model.Category;
import mike.sparkd.back_end_inventory_manager.category.Model.CategoryReadDto;
import mike.sparkd.back_end_inventory_manager.common.exception.NotFoundException;
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
        return categoryService.getAllCategoriesSortedByName().stream()
                .map(CategoryReadDto::from)
                .toList();
    }

    // GET /api/v1/categories/{id}
    @GetMapping("/{id}")
    public CategoryReadDto getById(@PathVariable @Positive long id) {
        Category category = categoryService.getCategoryById(id);
        return CategoryReadDto.from(category);
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
            throw new NotFoundException("Category " + id + " not found");
        }
    }
}