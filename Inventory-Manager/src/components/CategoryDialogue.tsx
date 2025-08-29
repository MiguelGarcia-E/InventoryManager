import { z } from "zod";
import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogTitle,
  Typography,
} from "@mui/material";
import TextFieldBox from "./TextFieldBox";
import { useEffect, useState } from "react";
import { useCategories } from "../context/CategoryContext";

const CategorySchema = z.object({
  name: z.string().trim().min(1, "Category name is required").max(120),
});

type Category = { id: number; name: string };

type Props = {
  open: boolean;
  onClose: () => void;
  mode: "create" | "edit";
  initialCategory?: Category; // for editing
  onSaved?: (cat: Category) => void;
};

export default function CategoryDialog({
  open,
  onClose,
  mode,
  initialCategory,
  onSaved,
}: Props) {
  const { categories, addCategory, updateCategory } = useCategories();

  const [name, setName] = useState("");
  const [error, setError] = useState<string | undefined>();
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (open) {
      setName(mode === "edit" ? initialCategory?.name ?? "" : "");
      setError(undefined);
    }
  }, [open, mode, initialCategory]);

  const handleSubmit = async () => {
    const parsed = CategorySchema.safeParse({ name });
    if (!parsed.success) {
      setError(parsed.error.issues[0]?.message ?? "Invalid name");
      return;
    }
    const trimmed = parsed.data.name;

    const exists = categories.some(
      (c) =>
        (c.name ?? "").trim().toLowerCase() === trimmed.toLowerCase() &&
        (mode === "create" || c.id !== initialCategory?.id)
    );
    if (exists) {
      setError("Category already exists");
      return;
    }

    try {
      setSubmitting(true);
      let saved: Category;
      if (mode === "create") {
        saved = await addCategory(trimmed); // POST
      } else {
        if (!initialCategory) throw new Error("Missing category to edit");
        saved = await updateCategory(initialCategory.id, trimmed); // PUT
      }
      onSaved?.(saved);
      onClose();
    } catch (e: any) {
      setError(
        e?.message ?? (mode === "create" ? "Create failed" : "Update failed")
      );
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      sx={{ "& .MuiDialog-paper": { width: "100%", maxWidth: 520, p: 2 } }}
    >
      <DialogTitle sx={{ pb: 1 }}>
        {mode === "create"
          ? "New Category"
          : `Edit Category #${initialCategory?.id}`}
      </DialogTitle>

      <Box
        component="form"
        onSubmit={(e) => {
          e.preventDefault();
          handleSubmit();
        }}
        sx={{ display: "flex", flexDirection: "column", gap: 2, px: 2, pb: 2 }}
      >
        <TextFieldBox
          lblString="Name"
          fieldString="Category name"
          value={name}
          onChange={(e) => {
            setName(e.target.value);
            setError(undefined);
          }}
        />
        {error && (
          <Typography variant="body2" color="error">
            {error}
          </Typography>
        )}

        <DialogActions sx={{ px: 0, pt: 1 }}>
          <Button onClick={onClose} disabled={submitting}>
            Cancel
          </Button>
          <Button type="submit" variant="contained" disabled={submitting}>
            {mode === "create" ? "Create" : "Save"}
          </Button>
        </DialogActions>
      </Box>
    </Dialog>
  );
}
