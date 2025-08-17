import { Box, Icon, IconButton, Paper, Typography } from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import {
  DataGrid,
  type GridColDef,
  type GridPaginationModel,
} from "@mui/x-data-grid";

type DataTableProps = {
  title: string;
  rows: any[];
  columns: GridColDef[];
  paginationModel?: GridPaginationModel;
  onEdit?: (row: any) => void;
  onDelete?: (row: any) => void;
};

export default function CatalogueTable({
  title = "Data Table",
  rows,
  columns,
  paginationModel = { pageSize: 10, page: 0 },
  onDelete,
  onEdit,
}: DataTableProps) {
  const actionColumns: GridColDef = {
    field: "actions",
    headerName: "Actions",
    width: 120,
    sortable: false,
    filterable: false,
    renderCell: (params) => (
      <div>
        {onEdit && (
          <IconButton
            size="small"
            onClick={(e) => {
              e.stopPropagation();
              onEdit(params.row);
            }}
          >
            <EditIcon sx={{ color: "#1e88e5" }}></EditIcon>
          </IconButton>
        )}
        {onDelete && (
          <IconButton
            color="error"
            size="small"
            onClick={(e) => {
              e.stopPropagation();
              onDelete(params.row);
            }}
          >
            <DeleteIcon></DeleteIcon>
          </IconButton>
        )}
      </div>
    ),
  };

  const finalColumns =
    !onEdit && !onDelete ? columns : [...columns, actionColumns];

  function daysUntilExpiration(date: string): number {
    const today = new Date();
    today.setHours(0, 0, 0, 0); // Set time to midnight for accurate comparison

    const expirationDate = new Date(date);
    expirationDate.setHours(0, 0, 0, 0);

    const timeDiff = expirationDate.getTime() - today.getTime();
    return Math.ceil(timeDiff / (1000 * 3600 * 24));
  }

  function getRowClassNameByDate(params: any) {
    //YYYY-MM-DD
    const date = params.row.expirationDate;
    if (!date) return "colorless-row";

    const days = daysUntilExpiration(date);

    if (days < 0) {
      return "red-row"; // Expired
    } else if (days < 7) {
      return "orange-row"; // Warning, less than a week
    } else if (days <= 14) {
      return "yellow-row"; // Valid, more than a week but less than a month
    }
    return "green-row";
  }

  return (
    <Box
      sx={{
        mx: "auto",
        width: "100%",
        p: 3,
        border: "2px solid #ccc",
        borderRadius: 2,
        backgroundColor: "#fff",
      }}
    >
      <Typography variant="h4" sx={{ mb: 1 }}>
        {title}
      </Typography>
      <DataGrid
        rows={rows} //Data
        columns={finalColumns} //Headers
        initialState={{ pagination: { paginationModel } }}
        pageSizeOptions={[10]}
        checkboxSelection
        getRowClassName={getRowClassNameByDate}
        sx={{
          border: 0,
          fontSize: "1rem",
          "& .MuiDataGrid-cell": { fontSize: "1rem" }, //celda
          "& .MuiDataGrid-columnHeaderTitle": {
            fontSize: "1.1rem",
            fontWeight: 600,
          }, // headers
          "& .MuiTablePagination-root": { fontSize: "0.9rem" }, // paginador
          "& .red-row": { backgroundColor: "#f00303ff", color: "white" },
          "& .orange-row": { backgroundColor: "#fc8b00ff" },
          "& .yellow-row": { backgroundColor: "#ffff30ff" },
          "& .green-row": { backgroundColor: "#83e502ff" },
        }}
      />
    </Box>
  );
}
