import { AppBar, Toolbar, Typography, Box, Tooltip, Link } from "@mui/material";

export default function Footer() {
  const year = new Date().getFullYear();

  return (
    <AppBar
      component="footer"
      position="static"
      color="primary"
      elevation={2}
      sx={{ mt: "auto" }}
    >
      <Toolbar sx={{ display: "flex", justifyContent: "space-between", py: 1 }}>
        {/* izquierda */}
        <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
          <Typography variant="body2">© {year} Inventory Manager</Typography>
        </Box>

        {/* derecha */}
        <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
          <Tooltip title="Encora">
            <Link
              href="https://www.encora.com"
              target="_blank"
              rel="noopener noreferrer"
              underline="none"
              sx={{
                display: "inline-flex",
                alignItems: "center",
                gap: 1,
                color: "inherit",
              }}
              aria-label="Ir a Encora"
            >
              <Box
                component="img"
                src="/EncoraLogo.png"
                alt="Encora Logo"
                sx={{ height: 28 }}
              />
            </Link>
          </Tooltip>
        </Box>
      </Toolbar>
    </AppBar>
  );
}
