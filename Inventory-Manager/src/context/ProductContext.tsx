import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useRef,
  useState,
  type ReactNode,
} from "react";
import {
  mapApiProduct,
  mapUItoApiProduct,
  type ApiProduct,
  type CatalogueParams,
  type CategoryInventorySummary,
  type Product,
} from "../dataTypes/product";
import { api } from "../api/client";

type PageResp<T> = {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
};

type ProductContextType = {
  //Products state
  products: Product[];
  isLoading: boolean;
  error: string | null;

  //Catalogue API call parameters
  params: CatalogueParams;
  setParams: React.Dispatch<React.SetStateAction<CatalogueParams>>;

  //Pagination info
  totalElements: number;
  totalPages: number;

  //Metrics
  metrics: CategoryInventorySummary[] | null;

  //Actions
  refreshProducts: () => Promise<Product[]>;
  fetchMetrics: () => Promise<CategoryInventorySummary[]>;
  createProduct: (product: Product) => Promise<Product>;
  updateProduct: (id: number, product: Product) => Promise<Product>;
  deleteProduct: (id: number) => Promise<void>;
};

const ProductContext = createContext<ProductContextType | null>(null);

export function ProductProvider({ children }: { children: ReactNode }) {
  const [products, setProducts] = useState<Product[]>([]);
  const [metrics, setMetrics] = useState<CategoryInventorySummary[] | null>(
    null
  );
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [params, setParams] = useState<CatalogueParams>({
    page: 1,
    size: 10,
    name: "",
    category: "",
    availability: "all",
    sortBy: "id",
    direction: "asc",
  });

  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const abortRef = useRef<AbortController | null>(null);

  const fetchProducts = useCallback(async () => {
    setIsLoading(true);
    setError(null);

    if (abortRef.current) {
      abortRef.current.abort();
    }
    abortRef.current = new AbortController();

    try {
      const { data } = await api.get<PageResp<ApiProduct>>("/products", {
        params: {
          page: params.page,
          size: params.size,
          name: params.name || "",
          category: params.category || "",
          availability: params.availability || "all",
          sortBy: params.sortBy || "id",
          direction: params.direction || "asc",
        },
        signal: abortRef.current.signal as any,
      });

      const list = (data?.content ?? []).map(mapApiProduct);
      setProducts(list);
      setTotalElements(data?.totalElements ?? 0);
      setTotalPages(data?.totalPages ?? 0);

      return list;
    } catch (err: any) {
      if (err.name !== "CanceledError" && err.code !== "ERR_CANCELED") {
        setError(
          (err as Error).message ?? "Unknown error when loading products"
        );
      }
      return [];
    } finally {
      setIsLoading(false);
    }
  }, [params]);

  const refreshProducts = useCallback(async () => {
    const list = await fetchProducts();
    return list ?? [];
  }, [fetchProducts]);

  const fetchMetrics = useCallback(async () => {
    try {
      const { data } = await api.get<CategoryInventorySummary[]>(
        "/products/metrics"
      );
      setMetrics(data ?? []);
      return data ?? [];
    } catch (err) {
      console.error("Error fetching metrics:", (err as Error).message);
      return [];
    }
  }, []);

  const createProduct = useCallback(
    async (product: Product) => {
      const { data } = await api.post<Product>(
        "/products",
        mapUItoApiProduct(product)
      );
      const created = mapApiProduct(data);
      await Promise.all([fetchProducts(), fetchMetrics()]);
      return created;
    },
    [fetchProducts, fetchMetrics]
  );

  const updateProduct = useCallback(
    async (id: number, product: Product) => {
      const { data } = await api.put<Product>(
        `/products/${id}`,
        mapUItoApiProduct(product)
      );
      const updated = mapApiProduct(data);

      await Promise.all([fetchProducts(), fetchMetrics()]);
      return updated;
    },
    [fetchProducts, fetchMetrics]
  );

  const deleteProduct = useCallback(
    async (id: number) => {
      await api.delete(`/products/${id}`);
      const list = await refreshProducts(); // Promise<Product[]>

      if (Array.isArray(list) && list.length === 0 && params.page > 1) {
        setParams((p) => ({ ...p, page: p.page - 1 }));
        await refreshProducts();
      }
      await fetchMetrics();
    },
    [fetchMetrics, params.page, refreshProducts, setParams]
  );

  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

  const value = useMemo<ProductContextType>(
    () => ({
      products,
      isLoading,
      error,
      params,
      setParams,
      totalElements,
      totalPages,
      metrics,
      refreshProducts,
      fetchMetrics,
      createProduct,
      updateProduct,
      deleteProduct,
    }),
    [
      products,
      isLoading,
      error,
      params,
      totalElements,
      totalPages,
      metrics,
      refreshProducts,
      fetchMetrics,
      createProduct,
      updateProduct,
      deleteProduct,
    ]
  );

  return (
    <ProductContext.Provider value={value}>{children}</ProductContext.Provider>
  );
}

export function useProductContext() {
  const context = useContext(ProductContext);
  if (!context) {
    throw new Error("useProductContext must be used within a ProductProvider");
  }
  return context;
}
