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
  {
    field: "stock",
    headerName: "Stock",
    type: "number",
    width: 120,
    renderCell: (params) => {
      const stock = params.value as number;
      let bg;
      if (stock < 5) bg = "#ff0000ff";
      if (stock < 10) bg = "#ed5407";
      return (
        <span
          style={{
            backgroundColor: bg,
            display: "block",
            width: "100%",
            height: "100%",
            padding: "4px 0",
            borderRadius: "4px",
            textAlign: "center",
          }}
        >
          {" "}
          {params.value}
        </span>
      );
    },
  },
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
    expirationDate: "2025-07-16",
    stock: 1,
  },
  {
    id: 3,
    name: "Cola Soda",
    category: productCategories[2],
    price: 1.2,
    expirationDate: "2025-08-20",
    stock: 200,
  },
  {
    id: 4,
    name: "Vitamin C Tablets",
    category: productCategories[3],
    price: 8.5,
    expirationDate: "",
    stock: 9,
  },
  {
    id: 5,
    name: "Broccoli",
    category: productCategories[0],
    price: 2.1,
    expirationDate: "2025-08-28",
    stock: 10,
  },
];

export const SummaryRows = [
  {
    category: "Food",
    units: 50,
    value: 75,
    avg: 1.5,
  },
  {
    category: "Clothing",
    units: 100,
    value: 4500,
    avg: 45,
  },
  {
    category: "Electronics",
    units: 30,
    value: 15000,
    avg: 500,
  },
  {
    category: "Toys",
    units: 20,
    value: 600,
    avg: 30,
  },
  {
    category: "Overall",
    units: 200,
    value: 20175,
    avg: 100.875,
  },
];
