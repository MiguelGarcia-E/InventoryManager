package mike.sparkd.back_end_inventory_manager.category;

import mike.sparkd.back_end_inventory_manager.category.Model.Category;
import mike.sparkd.back_end_inventory_manager.category.Repository.InMemoryCategoryRepository;
import mike.sparkd.back_end_inventory_manager.common.exception.BadRequestException;
import mike.sparkd.back_end_inventory_manager.common.exception.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryCategoryRepositoryTest {

    private InMemoryCategoryRepository repository;

    @BeforeEach
    void setUp() {
        // Cada instancia precarga 4 categorías en el constructor
        repository = new InMemoryCategoryRepository();
    }

    @Test
    void constructor_debePreCargarCuatroCategorias() {
        List<Category> categories = repository.getAllCategories();

        assertEquals(4, categories.size());

        assertEquals(1L, categories.get(0).getId());
        assertEquals("Certificación Agile/IT", categories.get(0).getName());

        assertEquals(2L, categories.get(1).getId());
        assertEquals("Certificación Cloud", categories.get(1).getName());

        assertEquals(3L, categories.get(2).getId());
        assertEquals("Certificación DevOps", categories.get(2).getName());

        assertEquals(4L, categories.get(3).getId());
        assertEquals("Certificación Networking", categories.get(3).getName());

        categories.forEach(c -> {
            assertNotNull(c.getCreationDate());
            assertNotNull(c.getUpdateDate());
        });
    }

    @Test
    void save_conNombreValido_debeAsignarIdFechasYRegistrarEnIndices() {
        int initialSize = repository.getAllCategories().size();

        Category c = new Category("Nueva Categoría");
        Category saved = repository.save(c);

        assertNotNull(saved.getId());
        assertEquals(initialSize + 1, repository.getAllCategories().size());

        assertNotNull(saved.getCreationDate());
        assertNotNull(saved.getUpdateDate());

        // existe por nombre, case-insensitive
        assertTrue(repository.existsByNameIgnoreCase("nueva categoría"));
        assertTrue(repository.existsByNameIgnoreCase("NUEVA CATEGORÍA"));
    }

    @Test
    void save_conNombreBlancoONull_debeLanzarBadRequestException() {
        Category c1 = new Category("   ");
        assertThrows(BadRequestException.class, () -> repository.save(c1));

        Category c2 = new Category(null);
        assertThrows(BadRequestException.class, () -> repository.save(c2));
    }

    @Test
    void save_conNombreDuplicadoIgnoreCase_debeLanzarConflictException() {
        int initialSize = repository.getAllCategories().size();

        // ya existe "Certificación Cloud" en el constructor
        Category c = new Category("   certificación cloud   ");
        assertThrows(ConflictException.class, () -> repository.save(c));

        assertEquals(initialSize, repository.getAllCategories().size());
    }

    @Test
    void update_categoriaExistenteMismoNombre_debeConservarCreationDateYActualizarObjeto() {
        Category original = repository.getAllCategories().get(0);
        Long id = original.getId();
        LocalDate originalCreationDate = original.getCreationDate();

        Category updated = new Category(original.getName());
        updated.setId(id);
        updated.setCreationDate(LocalDate.of(2000, 1, 1)); // debería ser sobrescrito
        updated.setUpdateDate(LocalDate.now().plusDays(1)); // el repo NO toca updateDate

        Category result = repository.update(updated);

        assertEquals(id, result.getId());
        assertEquals(original.getName(), result.getName());
        // creationDate debe seguir siendo la original
        assertEquals(originalCreationDate, result.getCreationDate());

        // sigue existiendo por nombre
        assertTrue(repository.existsByNameIgnoreCase(original.getName()));
    }

    @Test
    void update_categoriaNoExistente_debeLanzarConflictException() {
        Category c = new Category("No existe");
        c.setId(9999L);

        assertThrows(ConflictException.class, () -> repository.update(c));
    }

    @Test
    void update_conNombreBlancoONull_debeLanzarBadRequestException() {
        Category existing = repository.getAllCategories().get(0);

        Category updatedBlank = new Category("   ");
        updatedBlank.setId(existing.getId());
        assertThrows(BadRequestException.class, () -> repository.update(updatedBlank));

        Category updatedNull = new Category(null);
        updatedNull.setId(existing.getId());
        assertThrows(BadRequestException.class, () -> repository.update(updatedNull));
    }

    @Test
    void update_cambiandoNombreAUnoUnico_debeActualizarIndiceDeNombre() {
        Category original = repository.getAllCategories().get(0);
        Long id = original.getId();
        String oldName = original.getName();
        String newName = "Nombre Único Nuevo";

        Category updated = new Category(newName);
        updated.setId(id);

        Category result = repository.update(updated);

        assertEquals(newName, result.getName());

        // nuevo nombre debe existir
        assertTrue(repository.existsByNameIgnoreCase("nombre único nuevo"));

        // viejo nombre ya no debe existir
        assertFalse(repository.existsByNameIgnoreCase(oldName));
    }

    @Test
    void update_cambiandoNombreADuplicado_debeLanzarConflictExceptionYNoModificarNada() {
        List<Category> categories = repository.getAllCategories();
        Category cat1 = categories.get(0);
        Category cat2 = categories.get(1);

        // Intentar actualizar cat1 para que tenga el mismo nombre que cat2
        Category updated = new Category(cat2.getName());
        updated.setId(cat1.getId());

        assertThrows(ConflictException.class, () -> repository.update(updated));

        // Verificamos que ninguno fue modificado
        Category stillCat1 = repository.findById(cat1.getId()).orElseThrow();
        Category stillCat2 = repository.findById(cat2.getId()).orElseThrow();

        assertEquals(cat1.getName(), stillCat1.getName());
        assertEquals(cat2.getName(), stillCat2.getName());
    }

    @Test
    void deleteById_existente_debeRetornarTrueYRemoverDeIndices() {
        Category existing = repository.getAllCategories().get(0);
        Long id = existing.getId();
        String name = existing.getName();

        boolean result = repository.deleteById(id);

        assertTrue(result);
        assertTrue(repository.findById(id).isEmpty());
        assertFalse(repository.existsByNameIgnoreCase(name));
    }

    @Test
    void deleteById_noExistente_debeRetornarFalse() {
        boolean result = repository.deleteById(9999L);
        assertFalse(result);
    }

    @Test
    void getAllCategories_debeEstarOrdenadoPorIdAscendente() {
        // Insertamos un par más para que tenga sentido
        repository.save(new Category("Z extra"));
        repository.save(new Category("A extra"));

        List<Category> categories = repository.getAllCategories();
        List<Long> ids = categories.stream()
                .map(Category::getId)
                .collect(Collectors.toList());

        for (int i = 1; i < ids.size(); i++) {
            assertTrue(ids.get(i) > ids.get(i - 1),
                    "Lista no está ordenada por id ascendente");
        }
    }

    @Test
    void findById_existente_debeRetornarOptionalConValor() {
        Category existing = repository.getAllCategories().get(0);

        Optional<Category> found = repository.findById(existing.getId());

        assertTrue(found.isPresent());
        assertEquals(existing.getId(), found.get().getId());
        assertEquals(existing.getName(), found.get().getName());
    }

    @Test
    void findById_noExistente_debeRetornarOptionalVacio() {
        Optional<Category> found = repository.findById(9999L);
        assertTrue(found.isEmpty());
    }

    @Test
    void existsByNameIgnoreCase_debeSerCaseInsensitiveYTrimmear() {
        // del constructor: "Certificación DevOps"
        assertTrue(repository.existsByNameIgnoreCase("certificación devops"));
        assertTrue(repository.existsByNameIgnoreCase("  CERTIFICACIÓN DEVOPS  "));
        assertFalse(repository.existsByNameIgnoreCase("devops x"));
    }
}