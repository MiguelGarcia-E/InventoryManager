import { Outlet, Link } from "react-router-dom";
import { CssBaseline, Container, Box } from "@mui/material";
import TopBar from "../components/TopBar";
import Footer from "../components/Footer";

export default function MainLayout() {
  return (
    <>
      <CssBaseline />
      <TopBar />
      <Container maxWidth="lg" sx={{ mt: 4 }}>
        <Box
          component="main"
          sx={{
            bgcolor: "background.default",
            width: "100%",
            maxWidth: 1200,
            mx: "auto",
            px: 2,
            mt: 4,
            pb: 4,
            display: "grid",
            gap: 3,
          }}
        >
          <Outlet />
        </Box>
      </Container>
      <Footer></Footer>
    </>
  );
}
