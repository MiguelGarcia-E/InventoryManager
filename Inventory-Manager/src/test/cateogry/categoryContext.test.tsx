import React from "react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, waitFor } from "@testing-library/react";
import { act } from "react-dom/test-utils";

// 🔧 Mock de CategoryApi (antes de importar el provider)
vi.mock("../../api/category.api", () => {
  return {
    CategoryApi: {
      list: vi.fn(),
      create: vi.fn(),
      update: vi.fn(),
      remove: vi.fn(),
    },
  };
});

// 🔧 Mock simplificado de axios (solo usamos isCancel / isAxiosError)
vi.mock("axios", () => {
  return {
    __esModule: true,
    default: {
      isCancel: vi.fn().mockReturnValue(false),
      isAxiosError: vi.fn().mockReturnValue(false),
    },
  };
});

// IMPORTS DESPUÉS DE LOS MOCKS
import { CategoryApi } from "../../api/category.api";
import axios from "axios";
import type { CategoryReadDto } from "../../api/category.api";
import { CategoryProvider, useCategories } from "../../context/CategoryContext";

// guardamos el contexto para inspeccionarlo en los tests
let ctxRef: ReturnType<typeof useCategories> | null = null;

function TestConsumer() {
  const ctx = useCategories();
  ctxRef = ctx;
  return null;
}

beforeEach(() => {
  ctxRef = null;
  vi.clearAllMocks();
});

describe("CategoryContext", () => {
  it("carga categorías al montar y las ordena por nombre", async () => {
    const unordered: CategoryReadDto[] = [
      { id: 2, name: "pan" },
      { id: 1, name: "Agua" },
      { id: 3, name: "refrescos" },
    ];

    (CategoryApi.list as any).mockResolvedValueOnce(unordered);

    render(
      <CategoryProvider>
        <TestConsumer />
      </CategoryProvider>
    );

    await waitFor(() => {
      expect(ctxRef).not.toBeNull();
      expect(ctxRef!.loading).toBe(false);
    });

    expect(CategoryApi.list).toHaveBeenCalledTimes(1);

    // Deben estar ordenadas por nombre (case-insensitive, es-ES)
    const names = ctxRef!.categories.map((c) => c.name);
    expect(names).toEqual(["Agua", "pan", "refrescos"]);
    expect(ctxRef!.error).toBeNull();
  });

  it("setea error genérico cuando reload falla con error normal (no axios)", async () => {
    (CategoryApi.list as any).mockRejectedValueOnce(new Error("pum"));

    render(
      <CategoryProvider>
        <TestConsumer />
      </CategoryProvider>
    );

    await waitFor(() => {
      expect(ctxRef).not.toBeNull();
      expect(ctxRef!.loading).toBe(false);
      expect(ctxRef!.error).toBe("Error al cargar categorías");
      expect(ctxRef!.categories).toHaveLength(0);
    });
  });

  it("setea el mensaje de la API cuando reload falla con AxiosError", async () => {
    const axiosError = {
      isAxios: true,
      message: "Mensaje genérico",
      response: {
        data: {
          message: "Mensaje del backend",
        },
      },
    };

    (CategoryApi.list as any).mockRejectedValueOnce(axiosError);
    (axios.isCancel as any).mockReturnValue(false);
    (axios.isAxiosError as any).mockReturnValue(true);

    render(
      <CategoryProvider>
        <TestConsumer />
      </CategoryProvider>
    );

    await waitFor(() => {
      expect(ctxRef).not.toBeNull();
      expect(ctxRef!.loading).toBe(false);
      expect(ctxRef!.error).toBe("Mensaje del backend");
    });
  });

  it("ignora errores por cancelación (axios.isCancel = true)", async () => {
    const cancelError = { __isCancel: true };

    (CategoryApi.list as any).mockRejectedValueOnce(cancelError);
    (axios.isCancel as any).mockReturnValue(true);

    render(
      <CategoryProvider>
        <TestConsumer />
      </CategoryProvider>
    );

    await waitFor(() => {
      expect(ctxRef).not.toBeNull();
      expect(ctxRef!.loading).toBe(false);
      // no debería setear error porque se sale por el return
      expect(ctxRef!.error).toBeNull();
    });
  });

  it("addCategory crea categoría y actualiza la lista ordenada", async () => {
    // 1er list del useEffect
    (CategoryApi.list as any).mockResolvedValueOnce([]);

    const created: CategoryReadDto = { id: 10, name: "Lácteos" };
    (CategoryApi.create as any).mockResolvedValueOnce(created);

    render(
      <CategoryProvider>
        <TestConsumer />
      </CategoryProvider>
    );

    await waitFor(() => expect(ctxRef).not.toBeNull());

    let res: CategoryReadDto | undefined;
    await act(async () => {
      res = await ctxRef!.addCategory("Lácteos");
    });

    expect(CategoryApi.create).toHaveBeenCalledWith({ name: "Lácteos" });
    expect(res).toEqual(created);

    const names = ctxRef!.categories.map((c) => c.name);
    expect(names).toEqual(["Lácteos"]);
    expect(ctxRef!.error).toBeNull();
  });

  it("updateCategory actualiza la categoría y mantiene el orden", async () => {
    const initial: CategoryReadDto[] = [
      { id: 1, name: "Bebidas" },
      { id: 2, name: "Snacks" },
    ];

    (CategoryApi.list as any).mockResolvedValueOnce(initial);

    const updated: CategoryReadDto = { id: 2, name: "Abarrotes" };
    (CategoryApi.update as any).mockResolvedValueOnce(updated);

    render(
      <CategoryProvider>
        <TestConsumer />
      </CategoryProvider>
    );

    await waitFor(() => expect(ctxRef).not.toBeNull());

    await act(async () => {
      await ctxRef!.updateCategory(2, "Abarrotes");
    });

    expect(CategoryApi.update).toHaveBeenCalledWith(2, { name: "Abarrotes" });

    const names = ctxRef!.categories.map((c) => c.name);
    // Debe reordenar: "Abarrotes" va antes que "Bebidas"
    expect(names).toEqual(["Abarrotes", "Bebidas"]);
    expect(ctxRef!.error).toBeNull();
  });

  it("removeCategory elimina la categoría de la lista", async () => {
    const initial: CategoryReadDto[] = [
      { id: 1, name: "Bebidas" },
      { id: 2, name: "Snacks" },
    ];

    (CategoryApi.list as any).mockResolvedValueOnce(initial);
    (CategoryApi.remove as any).mockResolvedValueOnce(undefined);

    render(
      <CategoryProvider>
        <TestConsumer />
      </CategoryProvider>
    );

    await waitFor(() => expect(ctxRef).not.toBeNull());
    expect(ctxRef!.categories).toHaveLength(2);

    await act(async () => {
      await ctxRef!.removeCategory(1);
    });

    expect(CategoryApi.remove).toHaveBeenCalledWith(1);
    const ids = ctxRef!.categories.map((c) => c.id);
    expect(ids).toEqual([2]);
    expect(ctxRef!.error).toBeNull();
  });

  it("useCategories lanza error si se usa fuera del provider", () => {
    // truco: componente que solo llama al hook
    const Broken = () => {
      useCategories();
      return null;
    };

    // React va a tirar error en el render, lo capturamos con función
    expect(() =>
      render(
        <React.StrictMode>
          <Broken />
        </React.StrictMode>
      )
    ).toThrow("useCategories debe usarse dentro de <CategoryProvider>");
  });
});
