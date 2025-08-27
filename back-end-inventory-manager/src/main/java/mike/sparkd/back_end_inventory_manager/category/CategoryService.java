package mike.sparkd.back_end_inventory_manager.category;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.getAllCategories();
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