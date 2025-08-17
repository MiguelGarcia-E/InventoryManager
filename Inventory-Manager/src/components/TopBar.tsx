import { AppBar, Toolbar, Typography, Box, Tooltip } from "@mui/material";

export default function TopBar() {
  return (
    <AppBar position="static" color="primary" elevation={2}>
      <Toolbar sx={{ display: "flex", justifyContent: "space-between" }}>
        {/* Sección izquierda */}
        <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
          <Typography variant="h4" component="div">
            Inventory Manager
          </Typography>
        </Box>

        {/* Sección derecha */}
        <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
          <Tooltip title="Encora">
            <Tooltip title="Encora">
              <Box
                component="img"
                src="/EncoraLogo.png"
                alt="Encora Logo"
                sx={{ height: 40 }} // ajusta tamaño
              />
            </Tooltip>
          </Tooltip>
        </Box>
      </Toolbar>
    </AppBar>
  );
}
