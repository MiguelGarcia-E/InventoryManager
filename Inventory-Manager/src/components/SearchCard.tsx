import { Box, Button, Container, Typography } from "@mui/material";
import TextFieldBox from "./TextFieldBox";
import AutocompleteBox from "./AutocompleteBox";
import { useEffect, useMemo, useState } from "react";
import { useCategories } from "../context/CategoryContext";

type Params = {
  name: string;
  category: string;
  availability: "in" | "out" | "all";
  sortBy: "id" | "name" | "unitPrice" | "stock" | "expirationDate";
  direction: "asc" | "desc";
};

export default function SearchCard({
  value,
  onApply,
}: {
  value: Params;
  onApply: (v: Params) => void;
}) {
  const { categories, loading, error } = useCategories();

  const productCategories = useMemo(
    () =>
      [...new Set(categories.map((c) => c.name).filter(Boolean))].sort((a, b) =>
        a.localeCompare(b, "es", { sensitivity: "base" })
      ),
    [categories]
  );

  const [name, setName] = useState("");
  const [category, setCategory] = useState("");
  const [availability, setAvailability] = useState("");
  const [direction, setDirection] = useState<"asc" | "desc">("asc");
  const [sortBy, setSortBy] = useState<Params["sortBy"]>("id");

  useEffect(() => {
    setDirection(value.direction ?? "asc");
    setName(value.name ?? "");
    setCategory(value.category ?? "");
    setAvailability(
      value.availability === "in"
        ? "In Stock"
        : value.availability === "out"
        ? "Out of Stock"
        : "All"
    );
    setSortBy(value.sortBy ?? "id");
  }, [value]);

  const buildParams = (): Params => {
    const nameFilter = name.trim();
    const availabilityFilter =
      availability === "In Stock"
        ? "in"
        : availability === "Out of Stock"
        ? "out"
        : "";

    return {
      name: nameFilter || "",
      category: category || "",
      availability: availabilityFilter || "all",
      sortBy: sortBy || "id",
      direction,
    };
  };

  return (
    <Container
      maxWidth={false}
      disableGutters
      sx={{ width: "100%", mx: "auto" }}
    >
      <Box
        component="form"
        sx={{
          width: "100%",
          display: "flex",
          flexDirection: "column",
          gap: 2,
          p: 3,
          border: "2px solid #ccc",
          borderRadius: 2,
          backgroundColor: "#fff",
        }}
        onSubmit={(e) => {
          e.preventDefault();
          onApply(buildParams());
        }}
      >
        <Typography variant="h4" sx={{ mb: 1 }}>
          Search Bar
        </Typography>

        <TextFieldBox
          lblString="Name"
          fieldString="Certification name"
          width={"90%"}
          value={name}
          onChange={(e) => setName(e.target.value)}
        ></TextFieldBox>

        <AutocompleteBox
          lblString="Category"
          fieldString={
            loading ? "Loading Categories..." : "Certification category"
          }
          options={productCategories}
          width="50%"
          value={category || null}
          onChange={(_, value) => setCategory(value ?? "")}
        ></AutocompleteBox>

        <Box
          sx={{
            width: "100%",
            display: "flex",
            alignItems: "center",
            m: 0,
          }}
        >
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              gap: 2,
              width: "100%",
            }}
          >
            <AutocompleteBox
              lblString="Availability"
              fieldString="availability category"
              options={["In Stock", "Out of Stock", "All"]}
              value={availability || null}
              onChange={(_, value) => setAvailability(value ?? "")}
              width="55%"
            />

            <Button type="submit" variant="outlined">
              Search
            </Button>
          </Box>
        </Box>
      </Box>
    </Container>
  );
}
