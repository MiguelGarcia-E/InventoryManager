package mike.sparkd.back_end_inventory_manager.category;

import mike.sparkd.back_end_inventory_manager.product.Product;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Primary
public class InMemoryCategoryRepository implements CategoryRepository{
    private final ConcurrentHashMap<Long, Category> byId = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> nameIndex = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    private static String key(String s) { return s == null ? null : s.trim().toLowerCase(); }

    @Override
    public Category save(Category c) {
        String k = key(c.getName());
        if (k == null || k.isBlank()) throw new IllegalArgumentException("Blank name when saving");
        if (nameIndex.containsKey(k)) throw new IllegalArgumentException("Category selected name already exists ");

        long id = seq.getAndIncrement();
        LocalDate now = LocalDate.now();
        c.setId(id);
        c.setCreationDate(now);
        c.setUpdateDate(now);

        byId.put(id, c);
        nameIndex.put(k, id);
        return c;
    }

    @Override
    public Category update(Category c) {
        long id = c.getId();
        Category existing = byId.get(id);
        if (existing == null) throw new NoSuchElementException("category " + id + " does not exist");

        String oldK = key(existing.getName());
        String newK = key(c.getName());
        if (newK == null || newK.isBlank()) throw new IllegalArgumentException("Blank name when updating");

        // check other categories for the new name
        if (!newK.equals(oldK)) {
            Long clash = nameIndex.get(newK);
            if (clash != null && clash != id) throw new IllegalArgumentException("Category selected name already exists");
            if (oldK != null) nameIndex.remove(oldK);
            nameIndex.put(newK, id);
        }

        c.setCreationDate(existing.getCreationDate());
        byId.put(id, c);
        return c;
    }

    @Override
    public boolean deleteById(long id) {
        Category removed = byId.remove(id);
        if (removed == null) return false;
        String k = key(removed.getName());
        if (k != null) nameIndex.remove(k);
        return true;
    }

    @Override
    public List<Category> getAllCategories() {
        // Si quieres orden consistente: por id asc
        return byId.values().stream()
                .sorted(Comparator.comparing(Category::getId))
                .toList();
    }

    public boolean existsByNameIgnoreCase(String name) {
        return nameIndex.containsKey(key(name));
    }
}

