import { Icon, IconButton, Paper, Typography } from "@mui/material";
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

  return (
    <Paper
      variant="outlined"
      sx={{ height: "100%", width: "100%", p: 3, pb: 1 }}
    >
      <Typography variant="h5">{title}</Typography>
      <DataGrid
        rows={rows} //Data
        columns={finalColumns} //Headers
        initialState={{ pagination: { paginationModel } }}
        pageSizeOptions={[10]}
        checkboxSelection
        sx={{ border: 0 }}
      />
    </Paper>
  );
}
