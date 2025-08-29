
package mike.sparkd.back_end_inventory_manager.product;

import java.time.LocalDate;
import java.time.ZoneId;
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
        // Loading relative dates
        final ZoneId MX = ZoneId.of("America/Monterrey");
        final LocalDate EXP_GT_1M   = LocalDate.now(MX).plusMonths(3);           // > 1 mes
        final LocalDate EXP_1_TO_2M = LocalDate.now(MX).plusDays(5); // entre 1 y 2 meses
        final LocalDate EXP_LT_1M   = LocalDate.now(MX).plusDays(12);            // < 1 mes

// =================== Categoría: Certificación Cloud ===================
        this.save(new Product("AWS Cloud Practitioner (CLF-C02) - Exam Voucher", "Certificación Cloud", 100.0F, null, 0));                 // sin expiración
        this.save(new Product("Google Cloud Associate Cloud Engineer (ACE) - Exam Voucher", "Certificación Cloud", 125.0F, EXP_GT_1M, 15)); // >1 mes
        this.save(new Product("Microsoft Azure Fundamentals (AZ-900) - Exam Voucher", "Certificación Cloud", 99.0F, EXP_1_TO_2M, 25));      // 1-2 meses
        this.save(new Product("AWS Solutions Architect Associate (SAA-C03) - Exam Voucher", "Certificación Cloud", 150.0F, EXP_LT_1M, 0));  // <1 mes
        this.save(new Product("Google Cloud Professional Cloud Architect (PCA) - Exam Voucher", "Certificación Cloud", 200.0F, EXP_GT_1M, 10));
        this.save(new Product("Microsoft Azure Administrator (AZ-104) - Exam Voucher", "Certificación Cloud", 165.0F, EXP_1_TO_2M, 12));
        this.save(new Product("AWS Developer Associate (DVA-C02) - Exam Voucher", "Certificación Cloud", 140.0F, null, 0));               // sin expiración
        this.save(new Product("AWS SysOps Administrator Associate (SOA-C02) - Exam Voucher", "Certificación Cloud", 150.0F, EXP_LT_1M, 5));

// =================== Categoría: Certificación DevOps ===================
        this.save(new Product("Kubernetes CKA (Certified Kubernetes Administrator) - Exam Voucher", "Certificación DevOps", 395.0F, EXP_GT_1M, 0));
        this.save(new Product("Kubernetes CKAD (Certified Kubernetes Application Developer) - Exam Voucher", "Certificación DevOps", 395.0F, EXP_1_TO_2M, 8));
        this.save(new Product("Kubernetes CKS (Certified Kubernetes Security Specialist) - Exam Voucher", "Certificación DevOps", 395.0F, EXP_LT_1M, 5));
        this.save(new Product("HashiCorp Terraform Associate - Exam Voucher", "Certificación DevOps", 150.0F, null, 0));                  // sin expiración
        this.save(new Product("HashiCorp Vault Associate - Exam Voucher", "Certificación DevOps", 150.0F, EXP_1_TO_2M, 7));
        this.save(new Product("Google Cloud Professional DevOps Engineer - Exam Voucher", "Certificación DevOps", 200.0F, EXP_GT_1M, 4));
        this.save(new Product("Microsoft Azure DevOps Engineer Expert (AZ-400) - Exam Voucher", "Certificación DevOps", 195.0F, null, 0)); // sin expiración

// =================== Categoría: Certificación Networking ===================
        this.save(new Product("Cisco CCNA 200-301 - Exam Voucher", "Certificación Networking", 300.0F, EXP_GT_1M, 6));
        this.save(new Product("Cisco CCNP Enterprise (ENCOR 350-401) - Exam Voucher", "Certificación Networking", 400.0F, EXP_LT_1M, 0));
        this.save(new Product("Cisco DevNet Associate (DEVASC 200-901) - Exam Voucher", "Certificación Networking", 300.0F, EXP_1_TO_2M, 10));
        this.save(new Product("CompTIA Network+ (N10-009) - Exam Voucher", "Certificación Networking", 180.0F, null, 0));                 // sin expiración
        this.save(new Product("CompTIA Security+ (SY0-701) - Exam Voucher", "Certificación Networking", 250.0F, EXP_GT_1M, 12));
        this.save(new Product("Juniper JNCIA-Junos (JN0-104) - Exam Voucher", "Certificación Networking", 200.0F, EXP_1_TO_2M, 7));
        this.save(new Product("Aruba Certified Switching Associate (HPE6-A72) - Exam Voucher", "Certificación Networking", 210.0F, null, 0)); // sin expiración

// =================== Categoría: Certificación Agile/IT ===================
        this.save(new Product("Scrum.org PSM I (Professional Scrum Master I) - Exam Attempt", "Certificación Agile/IT", 150.0F, EXP_LT_1M, 0));
        this.save(new Product("Scrum.org PSM II (Professional Scrum Master II) - Exam Attempt", "Certificación Agile/IT", 200.0F, EXP_1_TO_2M, 6));
        this.save(new Product("Scrum.org PSPO I (Professional Scrum Product Owner I) - Exam Attempt", "Certificación Agile/IT", 150.0F, null, 10)); // sin expiración
        this.save(new Product("ITIL 4 Foundation - Exam Voucher", "Certificación Agile/IT", 200.0F, EXP_GT_1M, 0));
        this.save(new Product("COBIT 2019 Foundation - Exam Voucher", "Certificación Agile/IT", 220.0F, EXP_1_TO_2M, 5));
        this.save(new Product("SAFe Agilist (Leading SAFe) - Exam Voucher", "Certificación Agile/IT", 250.0F, null, 0));                 // sin expiración
        this.save(new Product("PMI Agile Certified Practitioner (PMI-ACP) - Exam Voucher", "Certificación Agile/IT", 300.0F, EXP_GT_1M, 4));
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

    public PageResponse<Product> getByParamsSearch(
            String name,             // contains (case-insensitive)
            String category,         // equals (case-insensitive)
            String availability,     // "in" | "out" | "all"/null
            int page,                // 1-based
            int size,                // > 0
            String sortBy,           // id|name|category|unitPrice|stock|expirationDate
            String direction         // asc|desc
    ) {
        if (page < 1) throw new IllegalArgumentException("page must be >= 1");
        if (size < 1) size = 10;

        //Filter
        var stream = productsMap.values().stream();

        if (name != null && !name.isBlank()){
            final String q = name.trim().toLowerCase();
            stream = stream.filter(p->{
                String n = p.getName();
                return n != null && n.toLowerCase().contains(q);
            });
        }
        if (category != null && !category.isBlank()){
            final String cat = category.trim();
            stream = stream.filter(p->cat.equalsIgnoreCase(p.getCategory()));
        }
        if(availability != null && !availability.isBlank()){
            final String a = availability.trim().toLowerCase();

            if ("all".equals(a)) {
                // sin filtro
            } else if("in".equalsIgnoreCase(availability)){
                stream = stream.filter(p->p.getStock() > 0);
            } else if ("out".equalsIgnoreCase(availability)){
                stream = stream.filter(p->p.getStock() <= 0);
            }
        }

        //Direciton
        Comparator<Product> cmp = buildSortComparator(sortBy, direction). thenComparing(Product::getId);
        List<Product> filtered = stream.sorted(cmp).toList();

        //PAginate
        long total = filtered.size();
        int from = (page - 1) * size;
        int to = Math.min(from + size, filtered.size());
        List<Product> content = (from >= filtered.size()) ? List.of() :filtered.subList(from, to);

        return new PageResponse<>(content, page, size, total);
    }

    private Comparator<Product> buildSortComparator(String sortBy, String direction) {
        Comparator<Product> cmp = switch (sortBy == null ? "id" : sortBy) {
            case "name" -> Comparator.comparing(p -> lower(p.getName()), Comparator.nullsLast(String::compareTo));
            case "category" -> Comparator.comparing(p -> lower(p.getCategory()), Comparator.nullsLast(String::compareTo));
            case "unitPrice" -> Comparator.comparingDouble(Product::getUnitPrice);
            case "stock" -> Comparator.comparingInt(Product::getStock);
            case "expirationDate" -> Comparator.comparing(Product::getExpirationDate, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(Product::getId); // id
        };
        if ("desc".equalsIgnoreCase(direction)) cmp = cmp.reversed();
        return cmp;
    }

    private static String lower(String s) { return s == null ? null : s.toLowerCase(); }



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
