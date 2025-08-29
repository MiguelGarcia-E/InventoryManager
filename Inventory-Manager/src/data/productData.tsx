import { type GridColDef } from "@mui/x-data-grid";

export const ProductColumns: GridColDef<ProductRow>[] = [
  { field: "name", headerName: "Name", flex: 1 },
  { field: "category", headerName: "Category", flex: 1 },
  { field: "unitPrice", headerName: "Price", type: "number", width: 120 },
  { field: "expirationDate", headerName: "Expiration Date", width: 150 },
  {
    field: "stock",
    headerName: "Stock",
    type: "number",
    width: 120,
    renderCell: (params) => {
      const stock = params.value as number;
      let bg;
      if (stock < 5) bg = "#ff5454ff";
      if (stock >= 5 && stock < 10) bg = "#ff9864ff";
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
