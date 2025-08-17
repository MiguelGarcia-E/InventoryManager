import { Box, Button, Container, Typography } from "@mui/material";
import TextFieldBox from "./TextFieldBox";
import AutocompleteBox from "./AutocompleteBox";
export default function SearchCard() {
  const productCategories = ["Vegetables", "Candy", "Soda Pops", "Suplements"];
  const availabilityCategories = ["In Stock", "Out of Stock", "All"];

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
      >
        <Typography variant="h4" sx={{ mb: 1 }}>
          Search Bar
        </Typography>
        <TextFieldBox
          lblString="Name"
          fieldString="Product name"
          width={"90%"}
        ></TextFieldBox>

        <AutocompleteBox
          lblString="Category"
          fieldString="product category"
          options={productCategories}
          width="50%"
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
              options={availabilityCategories}
              width="55%"
            />
            <Button variant="outlined">Search</Button>
          </Box>
        </Box>
      </Box>
    </Container>
  );
}
