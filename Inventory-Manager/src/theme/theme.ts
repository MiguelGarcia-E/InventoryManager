import { createTheme } from "@mui/material/styles";

export const theme = createTheme({
  palette: {
    primary: { main: "#481c7c" },
    secondary: { main: "#6193ff" },
    background: {default: "#f8f4fc", paper: "#ffffff"},
    error: { main: "#d32f2f" },
    success: { main: "#2e7d32" },
  },
  typography: {
    fontFamily: "'Nunito', sans-serif",
    button: {
      textTransform: "none", // evita el uppercase en botones
    },
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: "8px",      
          fontWeight: 800,
        },
        containedPrimary: {
          backgroundColor: "#481c7c",
          "&:hover": {
            backgroundColor: "#3a1663",
          },
        },
      },
    },

    MuiTextField: {
      styleOverrides: {
        root: {
          fontSize: "2rem",     
        },
      },
    },
}});