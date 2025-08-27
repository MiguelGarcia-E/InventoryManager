
package mike.sparkd.back_end_inventory_manager.product;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class InMemoryProductRepository implements ProductRepository {
    private final ConcurrentHashMap<Long, Product> productsMap = new ConcurrentHashMap();
    private final AtomicLong seq = new AtomicLong(1L);
    private int size;

    public InMemoryProductRepository() {
        this.size = 0;
        this.save(new Product("AWS Cloud Practitioner (CLF-C02) - Exam Voucher", "Certificación AWS", 100.0F, LocalDate.of(2026, 6, 30), 40));
        this.save(new Product("AWS Solutions Architect Associate (SAA-C03) - Exam Voucher", "Certificación AWS", 150.0F, LocalDate.of(2026, 12, 31), 35));
        this.save(new Product("Google Cloud Associate Cloud Engineer (ACE) - Exam Voucher", "Certificación Google Cloud", 125.0F, LocalDate.of(2026, 9, 30), 30));
        this.save(new Product("Google Cloud Professional Cloud Architect (PCA) - Exam Voucher", "Certificación Google Cloud", 200.0F, LocalDate.of(2026, 12, 31), 20));
        this.save(new Product("Microsoft Azure Fundamentals (AZ-900) - Exam Voucher", "Certificación Azure", 99.0F, LocalDate.of(2026, 8, 31), 50));
        this.save(new Product("Microsoft Azure Administrator (AZ-104) - Exam Voucher", "Certificación Azure", 165.0F, LocalDate.of(2026, 12, 31), 25));
        this.save(new Product("Kubernetes CKA (Certified Kubernetes Administrator) - Exam Voucher", "Certificación DevOps", 395.0F, LocalDate.of(2026, 11, 30), 10));
        this.save(new Product("HashiCorp Terraform Associate - Exam Voucher", "Certificación DevOps", 150.0F, LocalDate.of(2026, 10, 31), 18));
        this.save(new Product("Cisco CCNA 200-301 - Exam Voucher", "Certificación Networking", 300.0F, LocalDate.of(2026, 12, 31), 12));
        this.save(new Product("Scrum.org PSM I (Professional Scrum Master I) - Exam Attempt", "Certificación Agile", 150.0F, LocalDate.of(2026, 7, 31), 22));
        this.save(new Product("AWS Developer Associate (DVA-C02) - Exam Voucher", "Certificación AWS", 140.0F, LocalDate.of(2026, 5, 31), 28));
        this.save(new Product("AWS SysOps Administrator Associate (SOA-C02) - Exam Voucher", "Certificación AWS", 150.0F, LocalDate.of(2026, 4, 30), 24));
        this.save(new Product("AWS Solutions Architect Professional (SAP-C02) - Exam Voucher", "Certificación AWS", 300.0F, LocalDate.of(2026, 3, 31), 15));
        this.save(new Product("AWS Security Specialty (SCS-C02) - Exam Voucher", "Certificación AWS", 250.0F, LocalDate.of(2026, 2, 28), 14));
        this.save(new Product("Google Cloud Professional Data Engineer (PDE) - Exam Voucher", "Certificación Google Cloud", 200.0F, LocalDate.of(2026, 1, 31), 18));
        this.save(new Product("Google Cloud Professional DevOps Engineer - Exam Voucher", "Certificación Google Cloud", 200.0F, LocalDate.of(2026, 5, 31), 16));
        this.save(new Product("Google Cloud Professional Security Engineer - Exam Voucher", "Certificación Google Cloud", 200.0F, LocalDate.of(2026, 6, 30), 12));
        this.save(new Product("Microsoft Azure Security Engineer (AZ-500) - Exam Voucher", "Certificación Azure", 165.0F, LocalDate.of(2026, 3, 31), 22));
        this.save(new Product("Microsoft Azure DevOps Engineer Expert (AZ-400) - Exam Voucher", "Certificación Azure", 195.0F, LocalDate.of(2026, 4, 30), 18));
        this.save(new Product("Microsoft Azure Data Engineer (DP-203) - Exam Voucher", "Certificación Azure", 180.0F, LocalDate.of(2026, 5, 31), 20));
        this.save(new Product("Kubernetes CKAD (Certified Kubernetes Application Developer) - Exam Voucher", "Certificación DevOps", 395.0F, LocalDate.of(2026, 9, 30), 12));
        this.save(new Product("Kubernetes CKS (Certified Kubernetes Security Specialist) - Exam Voucher", "Certificación DevOps", 395.0F, LocalDate.of(2026, 10, 31), 8));
        this.save(new Product("HashiCorp Vault Associate - Exam Voucher", "Certificación DevOps", 150.0F, LocalDate.of(2026, 12, 31), 14));
        this.save(new Product("Linux Foundation LFCS (Linux Foundation Certified SysAdmin) - Exam Voucher", "Certificación Linux", 375.0F, LocalDate.of(2026, 8, 31), 10));
        this.save(new Product("Red Hat RHCSA (EX200) - Exam Voucher", "Certificación Linux", 400.0F, LocalDate.of(2026, 11, 30), 6));
        this.save(new Product("Cisco DevNet Associate (DEVASC 200-901) - Exam Voucher", "Certificación Networking", 300.0F, LocalDate.of(2026, 6, 30), 10));
        this.save(new Product("Cisco CCNP Enterprise (ENCOR 350-401) - Exam Voucher", "Certificación Networking", 400.0F, LocalDate.of(2026, 7, 31), 8));
        this.save(new Product("CompTIA Security+ (SY0-701) - Exam Voucher", "Certificación Seguridad", 250.0F, LocalDate.of(2026, 12, 31), 25));
        this.save(new Product("CompTIA Network+ (N10-009) - Exam Voucher", "Certificación Networking", 180.0F, LocalDate.of(2026, 9, 30), 20));
        this.save(new Product("ITIL 4 Foundation - Exam Voucher", "Certificación ITSM", 200.0F, LocalDate.of(2026, 8, 31), 26));
    }

    public Product save(Product product) {
        ++this.size;
        long id = this.seq.getAndIncrement();
        LocalDate now = LocalDate.now();
        product.setId(id);
        product.setCreationDate(now);
        product.setUpdateDate(now);
        this.productsMap.put(id, product);
        return product;
    }

    public Product update(Product product) {
        long id = product.getId();
        LocalDate now = LocalDate.now();
        return (Product)this.productsMap.compute(id, (k, existing) -> {
            if (existing == null) {
                throw new NoSuchElementException("product " + id + " not found");
            } else {
                product.setId(id);
                product.setCreationDate(existing.getCreationDate());
                product.setUpdateDate(now);
                return product;
            }
        });
    }

    public boolean deleteById(long id) {
        if (!this.productsMap.containsKey(id)) {
            return false;
        } else {
            this.productsMap.remove(id);
            --this.size;
            return true;
        }
    }

    public List<Product> getAllProducts() {
        return new ArrayList(this.productsMap.values());
    }

    public Optional<Product> getProductById(long id) {
        return Optional.ofNullable((Product)this.productsMap.get(id));
    }

    public List<Product> getFilteredAndPaginatedProducts(String filter, String filterTwo, int page, String direction) {
        if (page < 1) {
            throw new IllegalArgumentException();
        } else {
            Comparator<Product> comparator = this.buildComparator(filter, filterTwo, direction);
            List<Product> list = this.productsMap.values().stream().sorted(comparator).toList();
            int pageSize = 10;
            int from = (page - 1) * pageSize;
            int to = Math.min(from + pageSize, list.size());
            return from >= list.size() ? List.of() : list.subList(from, to);
        }
    }

    private Comparator<Product> buildComparator(String filter, String filterTwo, String direction) {
        Comparator<Product> finalFilter = this.switchComparatorCase(filter);
        if (filterTwo != null && !filterTwo.isBlank() && !filterTwo.equalsIgnoreCase(filter)) {
            finalFilter = finalFilter.thenComparing(this.switchComparatorCase(filterTwo));
        }

        if ("desc".equalsIgnoreCase(direction)) {
            finalFilter = finalFilter.reversed();
        }

        if (!equalsIgnoreCase(filter, "id") && !equalsIgnoreCase(filterTwo, "id")) {
            finalFilter = finalFilter.thenComparing(Product::getId);
        }

        return finalFilter;
    }

    private static boolean equalsIgnoreCase(String first, String second) {
        return first != null && first.equalsIgnoreCase(second);
    }

    private Comparator<Product> switchComparatorCase(String filter) {
        if (filter != null && !filter.isBlank()) {
            Comparator var10000;
            switch (filter) {
                case "name" -> var10000 = Comparator.comparing((Product p) -> p.getName().toLowerCase());
                case "category" -> var10000 = Comparator.comparing((Product p) -> p.getCategory().toLowerCase());
                case "unitPrice" -> var10000 = Comparator.comparing(Product::getUnitPrice);
                case "stock" -> var10000 = Comparator.comparing(Product::getStock);
                case "expirationDate" -> var10000 = Comparator.comparing(Product::getExpirationDate);
                default -> var10000 = Comparator.comparing(Product::getId);
            }

            return var10000;
        } else {
            return Comparator.comparing(Product::getId);
        }
    }

    public int getSize() {
        return this.size;
    }

    //METRICS
    //METRICS
    //METRICS
    //METRICS
    //METRICS




    public List<CategoryInventorySummary> getInventorySummaryByCategory(){
        //Category name with corresponding metrics
        Map<String, CategoryAccumulator> accumulatorMap = new HashMap<>();

        for (Product p: new ArrayList<>(productsMap.values())){
            String category = (p.getCategory() == null || p.getCategory().isBlank()) ? "NO-CATEGORY" : p.getCategory();
            //IF exists returns the maped pair, if not create it, insert it and return it
            CategoryAccumulator a = accumulatorMap.computeIfAbsent(category, k-> new CategoryAccumulator());
            int stock = p.getStock();
            double price = p.getUnitPrice();

            a.totalStock += stock;
            a.totalValue += price * stock;
        }

        List<CategoryInventorySummary> out = new ArrayList<>();
        accumulatorMap.forEach((category, acc) -> {
            double avgUnitPricer = acc.totalStock > 0 ? (acc.totalValue/acc.totalStock) : 0.0;
            avgUnitPricer = Math.round(avgUnitPricer * 100.0) / 100.0;
            out.add(new CategoryInventorySummary(category, acc.totalStock, acc.totalValue, avgUnitPricer));
        });

        out.sort(Comparator.comparing(CategoryInventorySummary::getCategory, String.CASE_INSENSITIVE_ORDER));
        return out;
    }

    private static class CategoryAccumulator {
        int totalStock;
        double totalValue;
    }


}
