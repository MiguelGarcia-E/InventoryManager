package mike.sparkd.back_end_inventory_manager.category.Repository;

import mike.sparkd.back_end_inventory_manager.category.Model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Category save(Category c);
    Category update(Category c);
    boolean deleteById(long id);
    List<Category> getAllCategories();
    Optional<Category> findById(long id);
}
