import {
  Box,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";
import type { CategoryInventorySummary } from "../dataTypes/product";

type MetricsTableProps = {
  title: string;
  metrics: CategoryInventorySummary[] | null;
};

export default function MetricsTable({
  title = "Inventory Summary",
  metrics,
}: MetricsTableProps) {
  const rows = Array.isArray(metrics) ? metrics : [];
  // console.log("MetricsTable rows:", metrics);

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
      <TableContainer>
        <Table
          sx={{
            "& .MuiTableCell-root": {
              fontSize: "1rem",
            },
            "& .MuiTableCell-head": {
              fontSize: "1.1rem",
              fontWeight: "bold",
            },
          }}
          size="medium"
          aria-label=""
        >
          <TableHead>
            <TableRow>
              <TableCell>Categories</TableCell>
              <TableCell align="right">Total Products in Stock</TableCell>
              <TableCell align="right">Total Value in Stock</TableCell>
              <TableCell align="right">Average Price in Stock</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {rows.map((metric) => (
              <TableRow
                key={metric.category}
                sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
              >
                <TableCell component="th" scope="row">
                  {metric.category}
                </TableCell>
                <TableCell align="right">{metric.totalUnitsInStock}</TableCell>
                <TableCell align="right">{metric.totalStockValue}</TableCell>
                <TableCell align="right">
                  {metric.averageUnitPriceInStock}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}
