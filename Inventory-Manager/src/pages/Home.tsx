import {
  Alert,
  Box,
  Button,
  Container,
  Snackbar,
  type SnackbarCloseReason,
} from "@mui/material";
import SearchCard from "../components/SearchCard";

import { ProductColumns } from "../data/productData";
import CatalogueTable from "../components/CatalogueTable";
import React, { useEffect, useMemo, useState } from "react";
import MetricsTable from "../components/MetricsTable";
import { useProductContext } from "../context/ProductContext";
import ProductDialog from "../components/ProductDialogue";
import type { Product } from "../dataTypes/product";

export default function Home() {
  //For creating and updating :)
  const [openDialogue, setOpenDialogue] = useState(false);
  const [editItem, setEditItem] = useState<Product | undefined>(undefined);

  const handleOpenCreate = () => {
    setEditItem(undefined);
    setOpenDialogue(true);
  };
  const handleOpenEdit = (row: Product) => {
    setEditItem(row);
    setOpenDialogue(true);
  };

  //DeletingToast
  // state
  const [openDelToast, setOpenDelToast] = useState(false);
  const [delMsg, setDelMsg] = useState("");

  const handleCloseDelToast = (
    _e: React.SyntheticEvent | Event,
    reason?: SnackbarCloseReason
  ) => {
    if (reason === "clickaway") return;
    setOpenDelToast(false);
  };

  const {
    products,
    isLoading,
    params,
    deleteProduct,
    totalElements,
    setParams,
    fetchMetrics,
    metrics,
  } = useProductContext();

  useEffect(() => {
    fetchMetrics();
  }, [fetchMetrics]);

  //Catalogue Table
  const rows = useMemo(() => {
    const nums = (v: any) => (v == null || v === "" ? 0 : Number(v));
    return (products ?? []).map((p) => ({
      id: p.id,
      name: p.name,
      category: p.category,
      unitPrice: nums(p.unitPrice),
      stock: nums(p.stock),
      expirationDate: p.expirationDate ?? "N/A",
    }));
  }, [products]);

  //PAgination
  const paginationModel = useMemo(
    () => ({
      page: Math.max(0, (params.page ?? 1) - 1),
      pageSize: params.size ?? 10,
    }),
    [params.page, params.size]
  );

  const handlePaginationChange = (model: {
    page: number;
    pageSize: number;
  }) => {
    setParams((p) => ({
      ...p,
      page: model.page + 1,
      size: model.pageSize,
    }));
  };

  const [_, setDeletingId] = useState<number | null>(null);

  const handleDelete = async (row: { id: number; name?: string }) => {
    if (!row?.id) return;
    const ok = confirm(`Delete ${row.id} - <${row.name ?? "Certification"}>?`);
    if (!ok) return;

    try {
      setDeletingId(row.id);
      await deleteProduct(row.id);
      setDelMsg(`Product ${row.id} - ${row.name ?? ""} successfully deleted`);
      setOpenDelToast(true);
    } catch (err: any) {
      alert(err?.message ?? "Product could not be deleted");
    } finally {
      setDeletingId(null);
    }
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
        <SearchCard
          value={{ direction: params.direction } as any}
          onApply={(v: any) =>
            setParams((p) => ({
              ...p,
              page: 1,
              name: v.name ?? "",
              category: v.category ?? "",
              availability: v.availability ?? "all",
              sortBy: v.sortBy ?? "id",
              direction: v.direction ?? "asc",
            }))
          }
        />
      </Box>

      {/* Botones superiores */}
      <Box
        sx={{
          display: "flex",
          gap: 1,
          justifyContent: "flex-start",
          width: "100%",
          mx: "auto",
        }}
      >
        <Button variant="outlined" onClick={handleOpenCreate}>
          New Certification
        </Button>
      </Box>

      <Box sx={{ mx: "auto", width: "100%" }}>
        <CatalogueTable
          title={"Certification catalogue"}
          rows={rows}
          columns={ProductColumns}
          loading={isLoading}
          //Pagination
          // selectionModel={}
          paginationModel={paginationModel}
          onPaginationModelChange={handlePaginationChange}
          rowCount={totalElements ?? 0}
          //Functions
          onDelete={handleDelete}
          onEdit={handleOpenEdit}
        />
      </Box>
      <Box sx={{ mx: "auto", width: "100%" }}>
        <MetricsTable title="Inventory Summary" metrics={metrics ?? []} />
      </Box>
      {/* DIALOGUES AND ALERTS---------------------------- */}
      <ProductDialog
        mode={editItem ? "edit" : "create"}
        initialValues={editItem}
        open={openDialogue}
        onClose={() => {
          setOpenDialogue(false);
          setEditItem(undefined);
        }}
      />
      <Snackbar
        open={openDelToast}
        autoHideDuration={5000}
        onClose={handleCloseDelToast}
        anchorOrigin={{ vertical: "bottom", horizontal: "left" }}
      >
        <Alert
          onClose={handleCloseDelToast}
          severity="success"
          variant="filled"
          sx={{ width: "100%" }}
        >
          {delMsg}
        </Alert>
      </Snackbar>
    </Container>
  );
}
