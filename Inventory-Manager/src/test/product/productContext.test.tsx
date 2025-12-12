import React from "react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, waitFor } from "@testing-library/react";
import { act } from "react-dom/test-utils";

// Mock del cliente API que usa el context
vi.mock("../../api/client", () => {
  const get = vi.fn();
  const post = vi.fn();
  const put = vi.fn();
  const del = vi.fn();
  return {
    api: { get, post, put, delete: del },
  };
});

// IMPORTAMOS DESPUÉS DEL MOCK
import { api } from "../../api/client";

import type {
  ApiProduct,
  CategoryInventorySummary,
  Product,
} from "../../dataTypes/product";
import {
  ProductProvider,
  useProductContext,
} from "../../context/ProductContext";

type ProductContextType = ReturnType<typeof useProductContext>;

// variable donde vamos a guardar el contexto para usarlo en los tests
let ctxRef: ProductContextType | null = null;

function TestConsumer() {
  const ctx = useProductContext();
  ctxRef = ctx;
  return null;
}

beforeEach(() => {
  ctxRef = null;
  vi.clearAllMocks();
});

describe("ProductContext", () => {
  it("carga productos al montar (useEffect inicial) y mapea los datos", async () => {
    const apiProduct: ApiProduct = {
      id: "1",
      name: "Leche",
      category: "Lácteos",
      unitPrice: "12.50",
      stock: "3",
      expirationDate: "2025-01-01",
    };

    // 1er GET: el de fetchProducts que se llama en el useEffect
    (api.get as any).mockResolvedValueOnce({
      data: {
        content: [apiProduct],
        page: 1,
        size: 10,
        totalElements: 1,
        totalPages: 1,
      },
    });

    render(
      <ProductProvider>
        <TestConsumer />
      </ProductProvider>
    );

    await waitFor(() => {
      expect(ctxRef).not.toBeNull();
      expect(ctxRef!.isLoading).toBe(false);
      expect(ctxRef!.products).toHaveLength(1);
    });

    const prod = ctxRef!.products[0];
    expect(prod).toMatchObject({
      id: 1,
      name: "Leche",
      category: "Lácteos",
      unitPrice: 12.5,
      stock: 3,
      expirationDate: "2025-01-01",
    });

    // Se llamó a /products con los params por default
    expect(api.get).toHaveBeenCalledWith(
      "/products",
      expect.objectContaining({
        params: expect.objectContaining({
          page: 1,
          size: 10,
          name: "",
          category: "",
          availability: "all",
          sortBy: "id",
          direction: "asc",
        }),
      })
    );

    expect(ctxRef!.totalElements).toBe(1);
    expect(ctxRef!.totalPages).toBe(1);
    expect(ctxRef!.error).toBeNull();
  });

  it("setea error cuando falla fetchProducts (error normal, no cancelado)", async () => {
    const err = new Error("Boom products");
    (api.get as any).mockRejectedValueOnce(err);

    render(
      <ProductProvider>
        <TestConsumer />
      </ProductProvider>
    );

    await waitFor(() => {
      expect(ctxRef).not.toBeNull();
      expect(ctxRef!.isLoading).toBe(false);
      expect(ctxRef!.error).toBe("Boom products");
      expect(ctxRef!.products).toHaveLength(0);
    });
  });

  it("fetchMetrics llama a la API y actualiza metrics", async () => {
    // 1er GET: fetchProducts del useEffect (lo dejamos vacío)
    const expectedMetrics: CategoryInventorySummary[] = [
      {
        category: "Snacks",
        totalUnitsInStock: 10,
        totalStockValue: 100,
        averageUnitPriceInStock: 10,
      },
    ];

    (api.get as any)
      .mockResolvedValueOnce({
        data: {
          content: [],
          page: 1,
          size: 10,
          totalElements: 0,
          totalPages: 0,
        },
      })
      // 2o GET: el de /products/metrics desde fetchMetrics()
      .mockResolvedValueOnce({
        data: expectedMetrics,
      });

    render(
      <ProductProvider>
        <TestConsumer />
      </ProductProvider>
    );

    await waitFor(() => expect(ctxRef).not.toBeNull());

    let data: CategoryInventorySummary[] = [];
    await act(async () => {
      data = await ctxRef!.fetchMetrics();
    });

    // Esperamos a que el setMetrics se aplique
    await waitFor(() => {
      expect(api.get).toHaveBeenLastCalledWith("/products/metrics");
      expect(data).toHaveLength(1);
      expect(ctxRef!.metrics).toEqual(expectedMetrics);
    });
  });

  it("createProduct hace POST, mapea la respuesta y refresca productos/metrics", async () => {
    // Orden de llamadas esperadas:
    // 1) GET /products (useEffect inicial)
    // 2) POST /products (createProduct)
    // 3) GET /products (fetchProducts dentro de createProduct)
    // 4) GET /products/metrics (fetchMetrics dentro de createProduct)

    (api.get as any)
      // 1) fetchProducts inicial
      .mockResolvedValueOnce({
        data: {
          content: [],
          page: 1,
          size: 10,
          totalElements: 0,
          totalPages: 0,
        },
      })
      // 3) fetchProducts después del create
      .mockResolvedValueOnce({
        data: {
          content: [],
          page: 1,
          size: 10,
          totalElements: 0,
          totalPages: 0,
        },
      })
      // 4) fetchMetrics después del create
      .mockResolvedValueOnce({
        data: [],
      });

    const apiCreated: ApiProduct = {
      id: "99",
      name: "Cerveza",
      category: "Bebidas",
      unitPrice: "25",
      stock: "6",
      expirationDate: "2026-01-01",
    };

    (api.post as any).mockResolvedValueOnce({
      data: apiCreated,
    });

    render(
      <ProductProvider>
        <TestConsumer />
      </ProductProvider>
    );

    await waitFor(() => expect(ctxRef).not.toBeNull());

    const newProd: Product = {
      id: 0, // el backend va a asignar el real
      name: "Cerveza",
      category: "Bebidas",
      unitPrice: 25,
      stock: 6,
      expirationDate: "2026-01-01",
    };

    let created: Product;
    await act(async () => {
      created = await ctxRef!.createProduct(newProd);
    });

    // Se mandó al backend el mapeo UI -> API
    expect(api.post).toHaveBeenCalledWith(
      "/products",
      expect.objectContaining({
        id: 0,
        name: "Cerveza",
        category: "Bebidas",
        unitPrice: 25,
        stock: 6,
        expirationDate: "2026-01-01",
      })
    );

    // La función regresa el Product mapeado desde ApiProduct
    expect(created!).toMatchObject({
      id: 99,
      name: "Cerveza",
      category: "Bebidas",
      unitPrice: 25,
      stock: 6,
      expirationDate: "2026-01-01",
    });

    // Confirmamos que intentó refrescar productos y métricas
    const productCalls = (api.get as any).mock.calls.filter(
      ([url]: any[]) => url === "/products"
    );
    expect(productCalls).toHaveLength(2);
    expect(
      (api.get as any).mock.calls.some(
        ([url]: any[]) => url === "/products/metrics"
      )
    ).toBe(true);
  });

  it("deleteProduct hace DELETE, refresca productos y métricas (sin cambiar de página cuando page = 1)", async () => {
    // Orden:
    // 1) GET /products (useEffect)
    // 2) DELETE /products/:id
    // 3) GET /products (refreshProducts, devuelve lista vacía)
    // 4) GET /products/metrics

    (api.get as any)
      // 1) fetchProducts inicial
      .mockResolvedValueOnce({
        data: {
          content: [],
          page: 1,
          size: 10,
          totalElements: 0,
          totalPages: 0,
        },
      })
      // 3) refreshProducts después del delete -> lista vacía
      .mockResolvedValueOnce({
        data: {
          content: [],
          page: 1,
          size: 10,
          totalElements: 0,
          totalPages: 0,
        },
      })
      // 4) fetchMetrics
      .mockResolvedValueOnce({
        data: [],
      });

    (api.delete as any).mockResolvedValueOnce({});

    render(
      <ProductProvider>
        <TestConsumer />
      </ProductProvider>
    );

    await waitFor(() => expect(ctxRef).not.toBeNull());

    const initialPage = ctxRef!.params.page;
    expect(initialPage).toBe(1);

    await act(async () => {
      await ctxRef!.deleteProduct(123);
    });

    // Se llamó al endpoint correcto
    expect(api.delete).toHaveBeenCalledWith("/products/123");

    // Dos llamadas a /products (inicial + refresh post-delete)
    const productCalls = (api.get as any).mock.calls.filter(
      ([url]: any[]) => url === "/products"
    );
    expect(productCalls).toHaveLength(2);

    // Llamada a métricas
    expect(
      (api.get as any).mock.calls.some(
        ([url]: any[]) => url === "/products/metrics"
      )
    ).toBe(true);

    // Como page era 1 y la lista queda vacía, NO debería decrementar
    expect(ctxRef!.params.page).toBe(1);
  });
});
