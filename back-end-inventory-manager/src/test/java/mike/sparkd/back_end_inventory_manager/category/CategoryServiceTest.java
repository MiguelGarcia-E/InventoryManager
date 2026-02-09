package mike.sparkd.back_end_inventory_manager.category;


import mike.sparkd.back_end_inventory_manager.category.Model.Category;
import mike.sparkd.back_end_inventory_manager.category.Repository.CategoryRepository;
import mike.sparkd.back_end_inventory_manager.category.Service.CategoryService;
import mike.sparkd.back_end_inventory_manager.common.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(categoryRepository);
    }

    @Test
    void getAllCategoriesSortedByName_debeOrdenarPorNombreCaseInsensitiveYTratarNullComoVacio() {
        Category c1 = new Category("zeta");
        c1.setId(1L);

        Category c2 = new Category("Alpha");
        c2.setId(2L);

        Category c3 = new Category(null);
        c3.setId(3L);

        when(categoryRepository.getAllCategories()).thenReturn(Arrays.asList(c1, c2, c3));

        List<Category> result = categoryService.getAllCategoriesSortedByName();

        assertEquals(3, result.size());
        // null name -> "" => va primero
        assertEquals(3L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId()); // "Alpha"
        assertEquals(1L, result.get(2).getId()); // "zeta"
    }

    @Test
    void getCategoryById_existente_debeRetornarCategoria() {
        Category c = new Category("Cloud");
        c.setId(10L);

        when(categoryRepository.findById(10L)).thenReturn(Optional.of(c));

        Category result = categoryService.getCategoryById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Cloud", result.getName());
        verify(categoryRepository, times(1)).findById(10L);
    }

    @Test
    void getCategoryById_noExistente_debeLanzarNotFoundException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> categoryService.getCategoryById(99L)
        );

        assertEquals("Category 99 not found", ex.getMessage());
        verify(categoryRepository, times(1)).findById(99L);
    }

    @Test
    void saveCategory_debeDelegarEnRepositoryYRetornarResultado() {
        Category toSave = new Category("DevOps");
        Category saved = new Category("DevOps");
        saved.setId(1L);

        when(categoryRepository.save(toSave)).thenReturn(saved);

        Category result = categoryService.saveCategory(toSave);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("DevOps", result.getName());
        verify(categoryRepository, times(1)).save(toSave);
    }

    @Test
    void updateCategory_debeDelegarEnRepositoryYRetornarResultado() {
        Category toUpdate = new Category("Networking");
        toUpdate.setId(5L);

        Category updated = new Category("Networking Updated");
        updated.setId(5L);

        when(categoryRepository.update(toUpdate)).thenReturn(updated);

        Category result = categoryService.updateCategory(toUpdate);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals("Networking Updated", result.getName());
        verify(categoryRepository, times(1)).update(toUpdate);
    }

    @Test
    void deleteCategoryById_debeDelegarEnRepositoryYRetornarBoolean() {
        when(categoryRepository.deleteById(7L)).thenReturn(true);

        boolean result = categoryService.deleteCategoryById(7L);

        assertTrue(result);
        verify(categoryRepository, times(1)).deleteById(7L);
    }

    @Test
    void deleteCategoryById_noExistente_debeRetornarFalse() {
        when(categoryRepository.deleteById(123L)).thenReturn(false);

        boolean result = categoryService.deleteCategoryById(123L);

        assertFalse(result);
        verify(categoryRepository, times(1)).deleteById(123L);
    }
}