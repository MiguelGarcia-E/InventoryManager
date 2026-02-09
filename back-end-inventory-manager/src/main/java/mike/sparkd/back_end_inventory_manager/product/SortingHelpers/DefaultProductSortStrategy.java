package mike.sparkd.back_end_inventory_manager.product.SortingHelpers;

import mike.sparkd.back_end_inventory_manager.product.Model.Product;

import java.util.Comparator;

public enum DefaultProductSortStrategy implements ProductSortStrategy {

    BY_ID {
        @Override
        public Comparator<Product> buildComparator(String direction) {
            Comparator<Product> cmp = Comparator.comparing(Product::getId);
            return applyDirection(cmp, direction);
        }
    },

    BY_NAME {
        @Override
        public Comparator<Product> buildComparator(String direction) {
            Comparator<Product> cmp = Comparator.comparing(
                    (Product p)  -> lower(p.getName()),
                    Comparator.nullsLast(String::compareTo)
            ).thenComparing(Product::getId);

            return applyDirection(cmp, direction);
        }
    },

    BY_CATEGORY {
        @Override
        public Comparator<Product> buildComparator(String direction) {
            Comparator<Product> cmp = Comparator.comparing(
                    (Product p)  -> lower(p.getCategory()),
                    Comparator.nullsLast(String::compareTo)
            ).thenComparing(Product::getId);

            return applyDirection(cmp, direction);
        }
    },

    BY_UNIT_PRICE {
        @Override
        public Comparator<Product> buildComparator(String direction) {
            Comparator<Product> cmp = Comparator.comparingDouble(Product::getUnitPrice)
                    .thenComparing(Product::getId);
            return applyDirection(cmp, direction);
        }
    },

    BY_STOCK {
        @Override
        public Comparator<Product> buildComparator(String direction) {
            Comparator<Product> cmp = Comparator.comparingInt(Product::getStock)
                    .thenComparing(Product::getId);
            return applyDirection(cmp, direction);
        }
    },

    BY_EXPIRATION_DATE {
        @Override
        public Comparator<Product> buildComparator(String direction) {
            Comparator<Product> cmp = Comparator.comparing(
                    Product::getExpirationDate,
                    Comparator.nullsLast(Comparator.naturalOrder())
            ).thenComparing(Product::getId);
            return applyDirection(cmp, direction);
        }
    };

    // Helper para aplicar dirección a todos
    protected Comparator<Product> applyDirection(Comparator<Product> cmp, String direction) {
        if ("desc".equalsIgnoreCase(direction)) {
            return cmp.reversed();
        }
        return cmp;
    }

    protected static String lower(String s) {
        return s == null ? null : s.toLowerCase();
    }

    public static DefaultProductSortStrategy fromSortBy(String sortBy) {
        if (sortBy == null) return BY_ID;
        return switch (sortBy) {
            case "name" -> BY_NAME;
            case "category" -> BY_CATEGORY;
            case "unitPrice" -> BY_UNIT_PRICE;
            case "stock" -> BY_STOCK;
            case "expirationDate" -> BY_EXPIRATION_DATE;
            default -> BY_ID;
        };
    }
}
