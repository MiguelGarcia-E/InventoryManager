import { Box, Button, Container } from "@mui/material";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";

import SearchCard from "../components/SearchCard";

import { ProductColumns, ProductRows } from "../data/productData";
import CatalogueTable from "../components/CatalogueTable";
import React from "react";
import CreateProductDialog from "../components/CreateProductdialog";
import { set } from "zod";

export default function Home() {
  const [openCreate, setOpenCreate] = React.useState(false);

  const handleOpenCreate = () => {
    setOpenCreate(true);
  };

  const handleCloseCreate = () => {
    setOpenCreate(false);
  };

  return (
    <Container
      sx={{
        width: "100%",
        gap: 2,
        display: "flex",
        flexDirection: "column",
      }}
    >
      {/* SearchCard centrado */}
      <Box sx={{ mx: "auto", width: "100%" }}>
        <SearchCard />
      </Box>

      {/* Botón alineado a la izquierda */}
      <Box
        sx={{
          display: "flex",
          justifyContent: "flex-start",
          width: "100%",
          mx: "auto",
        }}
      >
        <Button variant="outlined" onClick={handleOpenCreate}>
          New Product
        </Button>
        <CreateProductDialog open={openCreate} onClose={handleCloseCreate} />
      </Box>

      {/* Tabla centrada */}
      <Box sx={{ mx: "auto", width: "100%" }}>
        <CatalogueTable
          title={"Product catalogue"}
          rows={ProductRows}
          columns={ProductColumns}
          onDelete={(row) => alert(`Delete ${row.name}`)}
          onEdit={(row) => alert(`Edit ${row.name}`)}
        />
      </Box>
    </Container>
  );
}
