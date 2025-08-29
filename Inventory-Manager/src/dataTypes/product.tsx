export type SortingDirection = "asc" | "desc";

export type CatalogueParams = {
  page: number;
  category: string;
  name: string;
  availability: "all" | "in" | "out";
  sortBy: string;
  direction: SortingDirection;
  size: number;
};

export type CategoryInventorySummary = {
  category: string;
  totalUnitsInStock: number;
  totalStockValue: number;
  averageUnitPriceInStock: number;
};

export type Product = {
  id: number;
  name: string;
  unitPrice: number;
  stock: number;
  category: string;
  expirationDate?: string;
};

export const toNum = (v: unknown, def = 0): number => {
  if (v === null || v === undefined) return def;
  if (typeof v === "number") return Number.isFinite(v) ? v : def;
  if (typeof v === "string") {
    const s = v.trim().replace(/,/g, "."); // por si te llegan "12,34"
    const n = Number(s);
    return Number.isFinite(n) ? n : def;
  }
  return def;
};

export type ApiProduct = {
  id: number | string;
  name: string;
  category: string;
  unitPrice: number | string;
  expirationDate?: string | null; // ISO "YYYY-MM-DD"
  stock: number | string;
  creationDate?: string | null;
  updateDate?: string | null;
};

export const mapApiProduct = (p: ApiProduct): Product => ({
  id: Number(p.id),
  name: String(p.name ?? ""),
  category: String(p.category ?? ""),
  unitPrice: toNum(p.unitPrice, 0),
  stock: toNum(p.stock, 0),
  expirationDate: p.expirationDate ?? undefined,
});

export const mapUItoApiProduct = (
  p: Partial<Product>
): Partial<ApiProduct> => ({
  id: p.id,
  name: p.name,
  category: p.category,
  unitPrice: p.unitPrice,
  stock: p.stock,
  expirationDate: p.expirationDate ?? null,
});
