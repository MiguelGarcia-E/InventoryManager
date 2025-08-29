import { api } from "./client";

export interface CategoryReadDto { id: number; name: string }
export interface CategoryCreateDto { name: string }
export interface CategoryUpdateDto { name: string }

export const CategoryApi = {
  list: (signal?: AbortSignal) =>
    api.get<CategoryReadDto[]>("/categories", { signal }).then(r => r.data),

  get: (id: number, signal?: AbortSignal) =>
    api.get<CategoryReadDto>(`/categories/${id}`, { signal }).then(r => r.data),

  create: (payload: CategoryCreateDto) =>
    api.post<CategoryReadDto>("/categories", payload).then(r => r.data),

  update: (id: number, payload: CategoryUpdateDto) =>
    api.put<CategoryReadDto>(`/categories/${id}`, payload).then(r => r.data),

  remove: (id: number) =>
    api.delete<void>(`/categories/${id}`).then(() => undefined),
};
