package mike.sparkd.back_end_inventory_manager.product.SortingHelpers;

import mike.sparkd.back_end_inventory_manager.product.Model.Product;

import java.util.Comparator;

public interface ProductSortStrategy {
    Comparator<Product> buildComparator(String direction);
}
