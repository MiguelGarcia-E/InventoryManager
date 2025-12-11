package mike.sparkd.back_end_inventory_manager.category;

import mike.sparkd.back_end_inventory_manager.category.Model.Category;
import mike.sparkd.back_end_inventory_manager.category.Model.CategoryReadDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryReadDtoTest {

    @Test
    void from_debeMapearIdYNameDesdeCategory() {
        Category c = new Category("DevOps");
        c.setId(5L);

        CategoryReadDto dto = CategoryReadDto.from(c);

        assertEquals(5L, dto.id());
        assertEquals("DevOps", dto.name());
    }

    @Test
    void recordCategoryReadDto_debeGuardarValoresCorrectamente() {
        CategoryReadDto dto = new CategoryReadDto(10L, "Networking");

        assertEquals(10L, dto.id());
        assertEquals("Networking", dto.name());
    }

    @Test
    void equalsHashCodeYToString_debenFuncionarPorSerRecord() {
        CategoryReadDto dto1 = new CategoryReadDto(1L, "Cloud");
        CategoryReadDto dto2 = new CategoryReadDto(1L, "Cloud");
        CategoryReadDto dto3 = new CategoryReadDto(2L, "Cloud");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);

        String toString = dto1.toString();
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("Cloud"));
    }
}
