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
import { SummaryRows } from "../data/productData";

type MetricsTableProps = {
  title: string;
};

export default function MetricsTable({
  title = "Inventory Summary",
}: MetricsTableProps) {
  const rows = SummaryRows;

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
            {rows.map((row) => (
              <TableRow
                key={row.category}
                sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
              >
                <TableCell component="th" scope="row">
                  {row.category}
                </TableCell>
                <TableCell align="right">{row.units}</TableCell>
                <TableCell align="right">{row.value}</TableCell>
                <TableCell align="right">{row.avg}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}
