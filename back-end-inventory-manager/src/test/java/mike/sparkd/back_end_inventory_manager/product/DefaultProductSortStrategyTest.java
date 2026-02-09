package mike.sparkd.back_end_inventory_manager.product;

import mike.sparkd.back_end_inventory_manager.product.Model.Product;
import mike.sparkd.back_end_inventory_manager.product.SortingHelpers.DefaultProductSortStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DefaultProductSortStrategyTest {

    private Product p(long id, String name, String category, float price, LocalDate exp, int stock) {
        Product p = new Product(name, category, price, exp, stock);
        p.setId(id);
        return p;
    }

    // ---------- fromSortBy ----------

    @Test
    void fromSortBy_mapsKnownValuesAndDefaultsToById() {
        Assertions.assertEquals(DefaultProductSortStrategy.BY_ID, DefaultProductSortStrategy.fromSortBy(null));
        Assertions.assertEquals(DefaultProductSortStrategy.BY_ID, DefaultProductSortStrategy.fromSortBy("whatever"));

        Assertions.assertEquals(DefaultProductSortStrategy.BY_NAME, DefaultProductSortStrategy.fromSortBy("name"));
        Assertions.assertEquals(DefaultProductSortStrategy.BY_CATEGORY, DefaultProductSortStrategy.fromSortBy("category"));
        Assertions.assertEquals(DefaultProductSortStrategy.BY_UNIT_PRICE, DefaultProductSortStrategy.fromSortBy("unitPrice"));
        Assertions.assertEquals(DefaultProductSortStrategy.BY_STOCK, DefaultProductSortStrategy.fromSortBy("stock"));
        Assertions.assertEquals(DefaultProductSortStrategy.BY_EXPIRATION_DATE, DefaultProductSortStrategy.fromSortBy("expirationDate"));
    }

    // ---------- BY_ID ----------

    @Test
    void byId_sortsByIdAscAndDesc() {
        List<Product> list = new ArrayList<>();
        list.add(p(3L, "C", "Cat", 10f, null, 1));
        list.add(p(1L, "A", "Cat", 10f, null, 1));
        list.add(p(2L, "B", "Cat", 10f, null, 1));

        Comparator<Product> asc = DefaultProductSortStrategy.BY_ID.buildComparator("asc");
        list.sort(asc);

        Assertions.assertEquals(1L, list.get(0).getId());
        Assertions.assertEquals(2L, list.get(1).getId());
        Assertions.assertEquals(3L, list.get(2).getId());

        Comparator<Product> desc = DefaultProductSortStrategy.BY_ID.buildComparator("desc");
        list.sort(desc);

        Assertions.assertEquals(3L, list.get(0).getId());
        Assertions.assertEquals(2L, list.get(1).getId());
        Assertions.assertEquals(1L, list.get(2).getId());
    }

    // ---------- BY_NAME ----------

    @Test
    void byName_sortsCaseInsensitiveAndUsesIdAsTieBreaker() {
        Product p1 = p(2L, "b", "Cat", 10f, null, 1);
        Product p2 = p(1L, "B", "Cat", 10f, null, 1); // mismo nombre ignorando mayúsculas, menor id
        Product p3 = p(3L, "a", "Cat", 10f, null, 1);
        Product pNull = p(4L, null, "Cat", 10f, null, 1);

        List<Product> list = new ArrayList<>(List.of(p1, p2, p3, pNull));
        Comparator<Product> cmp = DefaultProductSortStrategy.BY_NAME.buildComparator("asc");
        list.sort(cmp);

        // "a" primero
        Assertions.assertEquals("a", list.get(0).getName());
        // "b"/"B" después, ordenados por id
        Assertions.assertEquals("B", list.get(1).getName());
        Assertions.assertEquals("b", list.get(2).getName());
        // null al final
        Assertions.assertNull(list.get(3).getName());
    }

    // ---------- BY_CATEGORY ----------

    @Test
    void byCategory_sortsCaseInsensitiveAndUsesIdAsTieBreaker() {
        Product p1 = p(2L, "X", "beta", 10f, null, 1);
        Product p2 = p(1L, "Y", "Beta", 10f, null, 1); // mismo category ignoring case, menor id
        Product p3 = p(3L, "Z", "alpha", 10f, null, 1);
        Product pNull = p(4L, "N", null, 10f, null, 1);

        List<Product> list = new ArrayList<>(List.of(p1, p2, p3, pNull));
        Comparator<Product> cmp = DefaultProductSortStrategy.BY_CATEGORY.buildComparator("asc");
        list.sort(cmp);

        // "alpha" primero
        Assertions.assertEquals("alpha", list.get(0).getCategory());
        // "beta"/"Beta" después, ordenados por id
        Assertions.assertEquals("Beta", list.get(1).getCategory());
        Assertions.assertEquals("beta", list.get(2).getCategory());
        // null al final
        Assertions.assertNull(list.get(3).getCategory());
    }

    // ---------- BY_UNIT_PRICE ----------

    @Test
    void byUnitPrice_sortsByPriceAndUsesIdAsTieBreaker() {
        Product p1 = p(2L, "X", "Cat", 10.0f, null, 1);
        Product p2 = p(1L, "Y", "Cat", 10.0f, null, 1); // mismo price, menor id
        Product p3 = p(3L, "Z", "Cat", 5.0f, null, 1);

        List<Product> list = new ArrayList<>(List.of(p1, p2, p3));
        Comparator<Product> cmp = DefaultProductSortStrategy.BY_UNIT_PRICE.buildComparator("asc");
        list.sort(cmp);

        // price 5.0 primero
        Assertions.assertEquals(5.0f, list.get(0).getUnitPrice(), 0.001);
        // luego los de 10.0 ordenados por id
        Assertions.assertEquals(1L, list.get(1).getId());
        Assertions.assertEquals(2L, list.get(2).getId());
    }

    // ---------- BY_STOCK ----------

    @Test
    void byStock_sortsByStockAndUsesIdAsTieBreaker() {
        Product p1 = p(2L, "X", "Cat", 10.0f, null, 3);
        Product p2 = p(1L, "Y", "Cat", 10.0f, null, 3); // mismo stock, menor id
        Product p3 = p(3L, "Z", "Cat", 10.0f, null, 1);

        List<Product> list = new ArrayList<>(List.of(p1, p2, p3));
        Comparator<Product> cmp = DefaultProductSortStrategy.BY_STOCK.buildComparator("asc");
        list.sort(cmp);

        // stock 1 primero
        Assertions.assertEquals(1, list.get(0).getStock());
        // luego los de stock 3, ordenados por id
        Assertions.assertEquals(1L, list.get(1).getId());
        Assertions.assertEquals(2L, list.get(2).getId());
    }

    // ---------- BY_EXPIRATION_DATE ----------

    @Test
    void byExpirationDate_sortsByDateAndPlacesNullLast() {
        LocalDate today = LocalDate.now();
        Product p1 = p(2L, "X", "Cat", 10.0f, today.plusDays(2), 1);
        Product p2 = p(1L, "Y", "Cat", 10.0f, today.plusDays(2), 1); // misma fecha, menor id
        Product p3 = p(3L, "Z", "Cat", 10.0f, today.plusDays(1), 1);
        Product pNull = p(4L, "N", "Cat", 10.0f, null, 1);

        List<Product> list = new ArrayList<>(List.of(p1, p2, p3, pNull));
        Comparator<Product> cmp = DefaultProductSortStrategy.BY_EXPIRATION_DATE.buildComparator("asc");
        list.sort(cmp);

        // fecha más cercana primero
        Assertions.assertEquals(today.plusDays(1), list.get(0).getExpirationDate());
        // luego los de misma fecha, ordenados por id
        Assertions.assertEquals(today.plusDays(2), list.get(1).getExpirationDate());
        Assertions.assertEquals(today.plusDays(2), list.get(2).getExpirationDate());
        Assertions.assertEquals(1L, list.get(1).getId());
        Assertions.assertEquals(2L, list.get(2).getId());
        // null al final
        Assertions.assertNull(list.get(3).getExpirationDate());
    }

    // ---------- Dirección desc aplicada de forma genérica ----------

    @Test
    void applyDirection_descReversesComparator() {
        Product p1 = p(1L, "A", "Cat", 10.0f, null, 1);
        Product p2 = p(2L, "B", "Cat", 20.0f, null, 1);

        List<Product> list = new ArrayList<>(List.of(p1, p2));
        Comparator<Product> asc = DefaultProductSortStrategy.BY_UNIT_PRICE.buildComparator("asc");
        list.sort(asc);
        Assertions.assertEquals(1L, list.get(0).getId()); // 10 primero

        Comparator<Product> desc = DefaultProductSortStrategy.BY_UNIT_PRICE.buildComparator("desc");
        list.sort(desc);
        Assertions.assertEquals(2L, list.get(0).getId()); // ahora 20 primero
    }
}
