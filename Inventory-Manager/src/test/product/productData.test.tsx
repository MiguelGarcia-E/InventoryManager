// src/test/product/productData.test.tsx
import React from "react";
import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";

// 👇 Mock de @mui/x-data-grid para que no intente cargar CSS ni nada raro
vi.mock("@mui/x-data-grid", () => {
  return {
    // No necesitamos nada en runtime, solo evitar que se importe el módulo real.
  };
});

import { ProductColumns } from "../../data/productData";

describe("ProductColumns", () => {
  it("tiene las columnas esperadas en orden", () => {
    const fields = ProductColumns.map((c) => c.field);

    expect(fields).toEqual([
      "name",
      "category",
      "unitPrice",
      "expirationDate",
      "stock",
    ]);
  });

  it("configura correctamente headers y tipos básicos", () => {
    const nameCol = ProductColumns[0];
    const categoryCol = ProductColumns[1];
    const priceCol = ProductColumns[2];
    const expirationCol = ProductColumns[3];
    const stockCol = ProductColumns[4];

    expect(nameCol.headerName).toBe("Name");
    expect(categoryCol.headerName).toBe("Category");
    expect(priceCol.headerName).toBe("Price");
    expect(expirationCol.headerName).toBe("Expiration Date");
    expect(stockCol.headerName).toBe("Stock");

    expect(priceCol.type).toBe("number");
    expect(stockCol.type).toBe("number");
    expect(priceCol.width).toBe(120);
    expect(expirationCol.width).toBe(150);
    expect(stockCol.width).toBe(120);
  });

  it("renderCell pinta rojo cuando stock < 5", () => {
    const stockCol = ProductColumns.find((c) => c.field === "stock");
    expect(stockCol).toBeDefined();
    expect(stockCol!.renderCell).toBeDefined();

    const element = stockCol!.renderCell!({ value: 3 } as any);
    render(element as React.ReactElement);

    const cell = screen.getByText("3");
    expect(cell).toHaveStyle("background-color: #ff5454ff");
  });

  it("renderCell pinta naranja cuando 5 <= stock < 10", () => {
    const stockCol = ProductColumns.find((c) => c.field === "stock");
    expect(stockCol).toBeDefined();
    expect(stockCol!.renderCell).toBeDefined();

    const element = stockCol!.renderCell!({ value: 7 } as any);
    render(element as React.ReactElement);

    const cell = screen.getByText("7");
    expect(cell).toHaveStyle("background-color: #ff9864ff");
  });

  it("renderCell no pone color especial cuando stock >= 10", () => {
    const stockCol = ProductColumns.find((c) => c.field === "stock");
    expect(stockCol).toBeDefined();
    expect(stockCol!.renderCell).toBeDefined();

    const element = stockCol!.renderCell!({ value: 12 } as any);
    render(element as React.ReactElement);

    const cell = screen.getByText("12");
    expect(cell).not.toHaveStyle("background-color: #ff5454ff");
    expect(cell).not.toHaveStyle("background-color: #ff9864ff");
  });
});
