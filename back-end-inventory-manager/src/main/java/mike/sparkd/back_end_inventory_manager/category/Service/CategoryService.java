package mike.sparkd.back_end_inventory_manager.category.Service;

import jakarta.validation.Valid;
import mike.sparkd.back_end_inventory_manager.category.Model.Category;
import mike.sparkd.back_end_inventory_manager.category.Repository.CategoryRepository;
import mike.sparkd.back_end_inventory_manager.common.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategoriesSortedByName() {
        return categoryRepository.getAllCategories().stream()
                .sorted(Comparator.comparing(
                        c -> c.getName() == null ? "" : c.getName(),
                        String.CASE_INSENSITIVE_ORDER
                ))
                .toList();
    }

    // 👇 NUEVO: obtener por id
    public Category getCategoryById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category " + id + " not found"));
    }

    public Category saveCategory(@Valid Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(@Valid Category category) {
        return categoryRepository.update(category);
    }

    public boolean deleteCategoryById(long id) {
        return categoryRepository.deleteById(id);
    }
}