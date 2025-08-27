import { Box, Button, Container } from "@mui/material";
import SearchCard from "../components/SearchCard";

import { ProductColumns, ProductRows } from "../data/productData";
import CatalogueTable from "../components/CatalogueTable";
import React, { useEffect } from "react";
import CreateProductDialog from "../components/CreateProductDialog";
import MetricsTable from "../components/MetricsTable";
import { useProductContext } from "../context/ProductContext";

export default function Home() {
  const [openCreate, setOpenCreate] = React.useState(false);

  const handleOpenCreate = () => {
    setOpenCreate(true);
  };

  const handleCloseCreate = () => {
    setOpenCreate(false);
  };

  const {
    products,
    isLoading,
    error,
    params,
    setParams,
    fetchMetrics,
    metrics,
  } = useProductContext();

  useEffect(() => {
    fetchMetrics();
    console.log("Fetched metrics:", metrics);
  }, [fetchMetrics]);

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

      <Box sx={{ mx: "auto", width: "100%" }}>
        <CatalogueTable
          title={"Product catalogue"}
          rows={ProductRows}
          columns={ProductColumns}
          onDelete={(row) => alert(`Delete ${row.name}`)}
          onEdit={(row) => alert(`Edit ${row.name}`)}
        />
      </Box>
      <Box sx={{ mx: "auto", width: "100%" }}>
        <MetricsTable title="Inventory Summary" metrics={metrics ?? []} />
      </Box>
    </Container>
  );
}
