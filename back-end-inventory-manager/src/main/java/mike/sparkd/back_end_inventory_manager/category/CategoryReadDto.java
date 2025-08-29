package mike.sparkd.back_end_inventory_manager.category;

public record CategoryReadDto(Long id, String name) {
    public static CategoryReadDto from(Category c) {
        return new CategoryReadDto(c.getId(), c.getName());
    }
}