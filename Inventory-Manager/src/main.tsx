import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import { RouterProvider } from "react-router-dom";
import { router } from "./routes/index.tsx";
import { ThemeProvider } from "@mui/material";
import { theme } from "./theme/theme.ts";
import { ProductProvider } from "./context/ProductContext.tsx";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <ProductProvider>
      <ThemeProvider theme={theme}>
        <RouterProvider router={router}></RouterProvider>
      </ThemeProvider>
    </ProductProvider>
  </StrictMode>
);
