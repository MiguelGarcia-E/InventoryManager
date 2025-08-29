import {
  createContext,
  useContext,
  useEffect,
  useMemo,
  useRef,
  useState,
} from "react";
import { CategoryApi } from "../api/category.api";
import type { CategoryReadDto } from "../api/category.api";

import axios from "axios";

type Ctx = {
  categories: CategoryReadDto[];
  loading: boolean;
  error: string | null;
  reload: () => Promise<void>;
  addCategory: (name: string) => Promise<CategoryReadDto>;
  updateCategory: (id: number, name: string) => Promise<CategoryReadDto>;
  removeCategory: (id: number) => Promise<void>;
};

const CategoryContext = createContext<Ctx | null>(null);

const sortByName = (arr: CategoryReadDto[]) =>
  [...arr].sort((a, b) =>
    (a?.name ?? "").localeCompare(b?.name ?? "", "es", { sensitivity: "base" })
  );

export function CategoryProvider({ children }: { children: React.ReactNode }) {
  const [categories, setCategories] = useState<CategoryReadDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const abortRef = useRef<AbortController | null>(null);

  async function reload() {
    setLoading(true);
    setError(null);
    abortRef.current?.abort();
    const controller = new AbortController();
    abortRef.current = controller;

    try {
      const data = await CategoryApi.list(controller.signal);
      setCategories(sortByName(data));
    } catch (err: unknown) {
      if (axios.isCancel(err)) return;
      if (axios.isAxiosError(err))
        setError(err.response?.data?.message ?? err.message);
      else setError("Error al cargar categorías");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    reload();
    return () => abortRef.current?.abort();
  }, []);

  async function addCategory(name: string) {
    setError(null);
    const created = await CategoryApi.create({ name });
    setCategories((prev) => sortByName([...prev, created]));
    return created;
  }

  async function updateCategory(id: number, name: string) {
    setError(null);
    const updated = await CategoryApi.update(id, { name });
    setCategories((prev) =>
      sortByName(prev.map((c) => (c.id === id ? updated : c)))
    );
    return updated;
  }

  async function removeCategory(id: number) {
    setError(null);
    await CategoryApi.remove(id);
    setCategories((prev) => prev.filter((c) => c.id !== id));
  }

  const value = useMemo<Ctx>(
    () => ({
      categories,
      loading,
      error,
      reload,
      addCategory,
      updateCategory,
      removeCategory,
    }),
    [categories, loading, error]
  );

  return (
    <CategoryContext.Provider value={value}>
      {children}
    </CategoryContext.Provider>
  );
}

export function useCategories() {
  const ctx = useContext(CategoryContext);
  if (!ctx)
    throw new Error("useCategories debe usarse dentro de <CategoryProvider>");
  return ctx;
}
