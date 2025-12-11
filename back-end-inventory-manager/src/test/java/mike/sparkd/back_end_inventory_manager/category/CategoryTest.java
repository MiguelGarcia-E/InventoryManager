package mike.sparkd.back_end_inventory_manager.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import mike.sparkd.back_end_inventory_manager.category.Model.Category;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void constructorVacio_debeInicializarCamposEnNull() {
        Category c = new Category();

        assertNull(c.getId());
        assertNull(c.getName());
        assertNull(c.getCreationDate());
        assertNull(c.getUpdateDate());
    }

    @Test
    void constructorConNombre_debeAsignarSoloNombre() {
        Category c = new Category("Backend");

        assertNull(c.getId());
        assertEquals("Backend", c.getName());
        assertNull(c.getCreationDate());
        assertNull(c.getUpdateDate());
    }

    @Test
    void gettersYSetters_debenFuncionarCorrectamente() {
        Category c = new Category();

        Long id = 10L;
        String name = "Cloud";
        LocalDate creation = LocalDate.of(2024, 1, 1);
        LocalDate update = LocalDate.of(2024, 2, 1);

        c.setId(id);
        c.setName(name);
        c.setCreationDate(creation);
        c.setUpdateDate(update);

        assertEquals(id, c.getId());
        assertEquals(name, c.getName());
        assertEquals(creation, c.getCreationDate());
        assertEquals(update, c.getUpdateDate());
    }

    @Test
    void campoName_debeTenerNotBlankYSizeCorrectos() throws NoSuchFieldException {
        Field nameField = Category.class.getDeclaredField("name");

        // NotBlank presente
        NotBlank notBlank = nameField.getAnnotation(NotBlank.class);
        assertNotNull(notBlank, "El campo 'name' debe tener @NotBlank");

        // Size.List con min=1 y max=120
        Size.List sizeList = nameField.getAnnotation(Size.List.class);
        assertNotNull(sizeList, "El campo 'name' debe tener @Size.List");

        Size[] sizes = sizeList.value();
        assertEquals(2, sizes.length, "Debería haber dos @Size en la lista");

        boolean hasMin1 = false;
        boolean hasMax120 = false;

        for (Size s : sizes) {
            if (s.min() == 1) {
                hasMin1 = true;
            }
            if (s.max() == 120) {
                hasMax120 = true;
            }
        }

        assertTrue(hasMin1, "Debe existir un @Size con min = 1");
        assertTrue(hasMax120, "Debe existir un @Size con max = 120");
    }
}
