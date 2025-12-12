// src/test/product/productDataTypes.test.ts
import { describe, it, expect } from "vitest";
import {
  toNum,
  mapApiProduct,
  mapUItoApiProduct,
  type ApiProduct,
  type Product,
} from "../../dataTypes/product";

// ---------- toNum ----------
describe("toNum", () => {
  it.each([
    [undefined, 0],
    [null, 0],
    [5, 5],
    [0, 0],
    [-3, -3],
    [NaN, 0],
    ["10", 10],
    ["  10  ", 10],
    ["12,34", 12.34],
    ["12.34", 12.34],
    ["", 0],
    ["   ", 0],
    ["abc", 0],
  ])("convierte %o en %o usando default 0", (input, expected) => {
    expect(toNum(input)).toBe(expected);
  });

  it("usa el default custom cuando no puede convertir", () => {
    expect(toNum("no-num", 999)).toBe(999);
    expect(toNum(NaN, 50)).toBe(50);
  });
});

// ---------- mapApiProduct ----------
describe("mapApiProduct", () => {
  it("mapea correctamente un ApiProduct con strings numéricos", () => {
    const api: ApiProduct = {
      id: "42",
      name: "Leche",
      category: "Lácteos",
      unitPrice: "12,50",
      stock: "7",
      expirationDate: "2025-01-01",
      creationDate: "2024-01-01",
      updateDate: "2024-01-02",
    };

    const result = mapApiProduct(api);

    expect(result).toEqual({
      id: 42,
      name: "Leche",
      category: "Lácteos",
      unitPrice: 12.5,
      stock: 7,
      expirationDate: "2025-01-01",
    });
  });

  it("usa defaults cuando vienen cosas raras", () => {
    const api: ApiProduct = {
      id: "no-num",
      name: "" as any,
      category: null as any,
      unitPrice: "abc",
      stock: NaN as any,
      expirationDate: null,
    };

    const result = mapApiProduct(api);

    expect(result.id).toBeNaN(); // Number('no-num') => NaN
    expect(result.name).toBe(""); // String('') => ''
    expect(result.category).toBe(""); // null ?? "" => "" => String("") => ""
    expect(result.unitPrice).toBe(0);
    expect(result.stock).toBe(0);
    expect(result.expirationDate).toBeUndefined();
  });

  it("deja expirationDate como undefined si no viene", () => {
    const api: ApiProduct = {
      id: 1,
      name: "Pan",
      category: "Panadería",
      unitPrice: 10,
      stock: 3,
      // sin expirationDate
    };

    const result = mapApiProduct(api);
    expect(result.expirationDate).toBeUndefined();
  });
});

// ---------- mapUItoApiProduct ----------
describe("mapUItoApiProduct", () => {
  it("pasa los campos tal cual del Product", () => {
    const ui: Product = {
      id: 1,
      name: "Refresco",
      category: "Bebidas",
      unitPrice: 15,
      stock: 20,
      expirationDate: "2024-12-31",
    };

    const api = mapUItoApiProduct(ui);

    expect(api).toEqual({
      id: 1,
      name: "Refresco",
      category: "Bebidas",
      unitPrice: 15,
      stock: 20,
      expirationDate: "2024-12-31",
    });
  });

  it("convierte expirationDate undefined a null, como espera la API", () => {
    const ui: Partial<Product> = {
      id: 2,
      name: "Galletas",
      category: "Snacks",
      unitPrice: 8,
      stock: 5,
      // sin expirationDate
    };

    const api = mapUItoApiProduct(ui);

    expect(api.expirationDate).toBeNull();
  });

  it("soporta partials (no revienta si faltan campos)", () => {
    const ui: Partial<Product> = {
      name: "Algo",
    };

    const api = mapUItoApiProduct(ui);

    expect(api).toEqual({
      id: undefined,
      name: "Algo",
      category: undefined,
      unitPrice: undefined,
      stock: undefined,
      expirationDate: null,
    });
  });
});
