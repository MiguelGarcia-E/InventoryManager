import { type GridColDef } from "@mui/x-data-grid";

export const productCategories = [
  "Vegetables",
  "Candy",
  "Soda Pops",
  "Suplements",
];

export const ProductColumns: GridColDef<ProductRow>[] = [
  { field: "name", headerName: "Name", flex: 1 },
  { field: "category", headerName: "Category", flex: 1 },
  { field: "price", headerName: "Price", type: "number", width: 120 },
  { field: "expirationDate", headerName: "Expiration Date", width: 150 },
  { field: "stock", headerName: "Stock", type: "number", width: 120 },
];

export type ProductRow = {
  id: number;
  category: string;
  name: string;
  price: number;
  expirationDate: string;
  stock: number;
};

export const ProductRows: ProductRow[] = [
  {
    id: 1,
    name: "Carrots",
    category: productCategories[0],
    price: 1.5,
    expirationDate: "2025-09-10",
    stock: 120,
  },
  {
    id: 2,
    name: "Chocolate Bar",
    category: productCategories[1],
    price: 2.99,
    expirationDate: "2025-12-01",
    stock: 50,
  },
  {
    id: 3,
    name: "Cola Soda",
    category: productCategories[2],
    price: 1.2,
    expirationDate: "2026-03-15",
    stock: 200,
  },
  {
    id: 4,
    name: "Vitamin C Tablets",
    category: productCategories[3],
    price: 8.5,
    expirationDate: "2027-05-30",
    stock: 80,
  },
  {
    id: 5,
    name: "Broccoli",
    category: productCategories[0],
    price: 2.1,
    expirationDate: "2025-08-28",
    stock: 65,
  },
];
