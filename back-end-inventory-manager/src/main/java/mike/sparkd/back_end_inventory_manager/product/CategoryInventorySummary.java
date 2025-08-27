package mike.sparkd.back_end_inventory_manager.product;

public class CategoryInventorySummary {
    private final String category;
    private final int totalUnitsInStock;
    private final double totalStockValue;
    private final double averageUnitPriceInStock;

    public CategoryInventorySummary(String category, int totalUnitsInStock, double totalStockValue, double averageUnitPriceInStock) {
        this.category = category;
        this.totalUnitsInStock = totalUnitsInStock;
        this.totalStockValue = totalStockValue;
        this.averageUnitPriceInStock = averageUnitPriceInStock;
    }

    public String getCategory() { return category; }
    public int getTotalUnitsInStock() { return totalUnitsInStock; }
    public double getTotalStockValue() { return totalStockValue; }
    public double getAverageUnitPriceInStock() { return averageUnitPriceInStock; }

}