import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Autocomplete,
  Box,
  Button,
  Dialog,
  DialogTitle,
  Typography,
} from "@mui/material";
import TextFieldBox from "./TextFieldBox";
import AutocompleteBox from "./AutocompleteBox";
import { productCategories } from "../data/productData";
import DatePickerBox from "./DatePickerBox";

const categories = productCategories;

const ProductSchema = z.object({
  name: z.string().min(2, "Product name (<2 characters) is required"),
  category: z.string().min(1, "Category is required"),
  unitPrice: z
    .number({
      error: (issue) =>
        issue.input === undefined
          ? "Requieres a number"
          : "Price must be a number",
    })
    .nonnegative("Price must be a non-negative number"),
  expirationDate: z.coerce
    .date({ error: "Invalid date format" })
    .refine((date) => date > new Date(), {
      message: "Expiration date must be in the future",
    }),
});

type CreateProductDialogProps = {
  open: boolean;
  onClose: () => void;
};

export default function CreateProductDialog(props: CreateProductDialogProps) {
  const { open, onClose } = props;

  const handleClose = () => {
    onClose();
  };

  return (
    <Dialog
      onClose={onClose}
      open={open}
      sx={{ "& .MuiDialog-paper": { width: "90%", maxWidth: 1000 } }} // ancho del dialog
    >
      <Box
        component={"form"}
        sx={{
          p: 3,
          display: "flex",
          flexDirection: "column",
          gap: 2,
        }}
      >
        <Typography variant="h5">Create a new product</Typography>
        {/* Form content goes here, using ProductSchema for validation */}
        <TextFieldBox
          lblString="Name"
          fieldString="Product name"
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
            options={categories}
            lblString="Category"
            fieldString="Existing categories"
          ></AutocompleteBox>
          <Button variant="outlined">New category</Button>
        </Box>

        <TextFieldBox
          lblString="Stock"
          fieldString="Product stock"
        ></TextFieldBox>

        <TextFieldBox
          lblString="Unit Price"
          fieldString="Unitarian price"
        ></TextFieldBox>

        <DatePickerBox lblString="Expiration Date:"></DatePickerBox>
        <Button variant="contained" onClick={handleClose}>
          Create
        </Button>
      </Box>
    </Dialog>
  );
}
