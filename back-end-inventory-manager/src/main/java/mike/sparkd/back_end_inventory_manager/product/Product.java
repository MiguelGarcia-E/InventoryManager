
package mike.sparkd.back_end_inventory_manager.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Size.List;
import java.time.LocalDate;

public class Product {
    private Long id;
    private @NotBlank @List({@Size(
            max = 120
    ), @Size(
            min = 1
    )}) String name;
    private @NotBlank String category;
    private @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "unitPrice must be > 0"
    ) float unitPrice;
    private @FutureOrPresent(
            message = "expirationDate must be today or in the future"
    ) LocalDate expirationDate;
    private @Min(
            value = 0L,
            message = "stock must be >= 0"
    ) int stock;
    private LocalDate creationDate;
    private LocalDate updateDate;

    public Product() {
    }

    public Product(String name, String category, float unitPrice, LocalDate expirationDate, int stock) {
        this.name = name;
        this.category = category;
        this.unitPrice = unitPrice;
        this.expirationDate = expirationDate;
        this.stock = stock;
    }

    public Product(String name, String category, float unitPrice, int stock) {
        this.name = name;
        this.category = category;
        this.unitPrice = unitPrice;
        this.stock = stock;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getUnitPrice() {
        return this.unitPrice;
    }

    public void setUnitPrice(float unitPrice) {
        this.unitPrice = unitPrice;
    }

    public LocalDate getExpirationDate() {
        return this.expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getStock() {
        return this.stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public LocalDate getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getUpdateDate() {
        return this.updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }
}
