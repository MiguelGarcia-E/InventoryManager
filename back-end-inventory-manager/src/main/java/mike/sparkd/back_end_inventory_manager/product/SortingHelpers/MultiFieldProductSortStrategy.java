package mike.sparkd.back_end_inventory_manager.product.SortingHelpers;

import mike.sparkd.back_end_inventory_manager.product.Model.Product;

import java.util.Comparator;

public class MultiFieldProductSortStrategy implements ProductSortStrategy {

    private final String primary;
    private final String secondary;

    public MultiFieldProductSortStrategy(String primary, String secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    @Override
    public Comparator<Product> buildComparator(String direction) {
        DefaultProductSortStrategy primaryStrategy =
                DefaultProductSortStrategy.fromSortBy(primary);
        DefaultProductSortStrategy secondaryStrategy =
                DefaultProductSortStrategy.fromSortBy(secondary);

        Comparator<Product> cmp = primaryStrategy.buildComparator("asc"); // dirección se aplica al final
        if (secondary != null && !secondary.isBlank() && !secondary.equalsIgnoreCase(primary)) {
            cmp = cmp.thenComparing(secondaryStrategy.buildComparator("asc"));
        }

        // siempre terminamos con id para estabilidad
        cmp = cmp.thenComparing(DefaultProductSortStrategy.BY_ID.buildComparator("asc"));

        if ("desc".equalsIgnoreCase(direction)) {
            cmp = cmp.reversed();
        }
        return cmp;
    }
}
