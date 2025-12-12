import { z } from "zod";
import { Box, Button, Dialog, Typography } from "@mui/material";
import TextFieldBox from "./TextFieldBox";
import AutocompleteBox from "./AutocompleteBox";
import DatePickerBox from "./DatePickerBox";
import { useEffect, useMemo, useState } from "react";
import { useProductContext } from "../context/ProductContext";
import type { Product } from "../dataTypes/product";
import { useCategories } from "../context/CategoryContext";
import CategoryDialog from "./CategoryDialogue";

const todayISO = new Date().toISOString().split("T")[0];

const ProductSchema = z.object({
  name: z.string().min(1, "Product name (<2 characters) is required").max(120),
  category: z.string().min(1, "Category is required"),
  unitPrice: z.number().int().min(0, "Unit price must be 0 or greater"),
  stock: z.number().int().min(0, "Stock must be 0 or greater"),
  expirationDate: z
    .string()
    .optional()
    .refine((date) => !date || date > todayISO, {
      message: "Expiration date must be after today",
    }),
});

type ProductFormData = z.infer<typeof ProductSchema>;

type ProductDialogProps = {
  open: boolean;
  onClose: () => void;
  mode: "create" | "edit";
  initialValues?: Product;
};

export default function ProductDialog({
  open,
  mode,
  initialValues,
  onClose,
}: ProductDialogProps) {
  const { createProduct, updateProduct } = useProductContext();

  const [catOpen, setCatOpen] = useState(false); // create
  const [editCatOpen, setEditCatOpen] = useState(false); // edit

  const [formData, setFormData] = useState<ProductFormData>({
    name: "",
    category: "",
    unitPrice: 0,
    stock: 0,
    expirationDate: "",
  });

  const [_, setErrors] = useState<
    Record<keyof ProductFormData, string | undefined>
  >({
    name: undefined,
    category: undefined,
    unitPrice: undefined,
    stock: undefined,
    expirationDate: undefined,
  });

  const [submitting, setSubmitting] = useState(false);

  //When in edit mode preload the items
  useEffect(() => {
    if (open && initialValues && mode === "edit") {
      setFormData({
        name: initialValues.name,
        category: initialValues.category,
        unitPrice: initialValues.unitPrice,
        stock: initialValues.stock,
        expirationDate: initialValues.expirationDate,
      });
    } else if (open && mode === "create") {
      setFormData({
        name: "",
        category: "",
        unitPrice: 0,
        stock: 0,
        expirationDate: undefined,
      });
    }
  }, [open, mode, initialValues]);

  //
  const setField = <K extends keyof ProductFormData>(
    field: K,
    value: ProductFormData[K]
  ) => {
    setFormData((fd) => ({ ...fd, [field]: value }));
  };

  const resetForm = () => {
    setFormData({
      name: "",
      category: "",
      unitPrice: 0,
      stock: 0,
      expirationDate: "",
    });
    setErrors({
      name: undefined,
      category: undefined,
      unitPrice: undefined,
      stock: undefined,
      expirationDate: undefined,
    });
  };

  const handleSubmit = async () => {
    const parsed = ProductSchema.safeParse(formData);
    if (!parsed.success) {
      const fieldErrors: Partial<Record<keyof ProductFormData, string>> = {};
      for (const err of parsed.error.issues) {
        const field = err.path[0] as keyof ProductFormData;
        fieldErrors[field] = err.message;
      }
      setErrors((prev) => ({ ...prev, ...fieldErrors }));
      return;
    }

    try {
      setSubmitting(true);
      if (mode === "create") {
        await createProduct({
          id: 0,
          name: parsed.data.name,
          category: parsed.data.category,
          unitPrice: parsed.data.unitPrice,
          stock: parsed.data.stock,
          expirationDate: parsed.data.expirationDate,
        });
      } else {
        if (!initialValues) throw new Error("Missing product data to update");
        await updateProduct(initialValues.id, {
          id: 0,
          name: parsed.data.name,
          category: parsed.data.category,
          unitPrice: parsed.data.unitPrice,
          stock: parsed.data.stock,
          expirationDate: parsed.data.expirationDate,
        });
      }

      resetForm();
      onClose();
    } catch (e: any) {
      alert(
        e?.message ?? (mode === "create" ? "Create failed" : "Update failed")
      );
    } finally {
      setSubmitting(false);
    }
  };

  const { categories, loading: catLoading, error: catError } = useCategories();
  const categoryOptions = useMemo(
    () =>
      [...new Set(categories.map((c) => c.name).filter(Boolean))].sort((a, b) =>
        a.localeCompare(b, "es", { sensitivity: "base" })
      ),
    [categories]
  );

  const selectedCat = useMemo(() => {
    const sel = (formData.category ?? "").trim().toLowerCase();
    if (!sel) return undefined;
    return categories.find((c) => (c.name ?? "").trim().toLowerCase() === sel);
  }, [formData.category, categories]);

  return (
    <Dialog
      onClose={onClose}
      open={open}
      sx={{ "& .MuiDialog-paper": { width: "90%", maxWidth: 1000 } }}
    >
      <Box
        component={"form"}
        sx={{
          p: 3,
          display: "flex",
          flexDirection: "column",
          gap: 2,
        }}
        onSubmit={(e) => {
          e.preventDefault();
          handleSubmit();
        }}
      >
        <Typography variant="h5">
          {mode === "create"
            ? "Create a new Certification"
            : `Edit certification #${initialValues?.id} - ${initialValues?.name}`}
        </Typography>

        <TextFieldBox
          lblString="Name"
          fieldString="Certification name"
          value={formData.name}
          onChange={(e) => {
            setField("name", e.target.value);
            setErrors((errs) => ({ ...errs, name: undefined }));
          }}
        ></TextFieldBox>
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            gap: 2,
            width: "100%",
          }}
        >
          <AutocompleteBox
            options={categoryOptions}
            lblString="Category"
            fieldString={
              catLoading
                ? "Loading categories..."
                : catError
                ? "Failed to load categories"
                : "Existing categories"
            }
            value={formData.category}
            onChange={(_, v) => {
              setField("category", v || "");
              setErrors((errs) => ({ ...errs, category: undefined }));
            }}
          ></AutocompleteBox>
          {/* Editar categoría seleccionada */}
          <Button
            variant="outlined"
            onClick={() => setEditCatOpen(true)}
            disabled={!selectedCat || catLoading}
          >
            Edit category
          </Button>
          <CategoryDialog
            open={editCatOpen}
            onClose={() => setEditCatOpen(false)}
            mode="edit"
            initialCategory={selectedCat}
            onSaved={(cat) => {
              setField("category", cat.name);
            }}
          />

          {/* Crear categoría */}

          <Button
            variant="outlined"
            onClick={() => setCatOpen(true)}
            disabled={catLoading}
          >
            New category
          </Button>
          <CategoryDialog
            open={catOpen}
            onClose={() => setCatOpen(false)}
            mode="create"
            onSaved={(cat) => {
              setField("category", cat.name);
            }}
          />
        </Box>

        <TextFieldBox
          lblString="Stock"
          fieldString="Product stock"
          type="number"
          value={formData.stock}
          onChange={(e) => {
            setField("stock", Number(e.target.value));
            setErrors((errs) => ({ ...errs, stock: undefined }));
          }}
        ></TextFieldBox>

        <TextFieldBox
          lblString="Unit Price"
          fieldString="Unit price"
          type="number"
          value={formData.unitPrice}
          onChange={(e) => {
            setField("unitPrice", Number(e.target.value) || 0);
            setErrors((errs) => ({ ...errs, price: undefined }));
          }}
        ></TextFieldBox>

        <DatePickerBox
          lblString="Expiration Date:"
          value={formData.expirationDate}
          onChange={(iso) => {
            setField("expirationDate", iso ?? undefined);
            setErrors((errs) => ({ ...errs, expirationDate: undefined }));
          }}
        ></DatePickerBox>
        <Button type="submit" disabled={submitting} variant="contained">
          {mode === "create" ? "Create" : `Save`}
        </Button>
      </Box>
    </Dialog>
  );
}
