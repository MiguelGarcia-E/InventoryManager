package mike.sparkd.back_end_inventory_manager.category.Model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class Category {
    private Long id;
    private @NotBlank
    @Size.List({@Size(
            max = 120
    ), @Size(
            min = 1
    )}) String name;

    private LocalDate creationDate;
    private LocalDate updateDate;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }
}
