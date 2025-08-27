package mike.sparkd.back_end_inventory_manager.category;

import java.util.List;

public interface CategoryRepository {
    Category save(Category c);
    Category update(Category c);
    boolean deleteById(long id);
    List<Category> getAllCategories();
}
