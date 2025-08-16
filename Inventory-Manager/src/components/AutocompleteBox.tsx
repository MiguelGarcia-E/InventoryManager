import { Autocomplete, Box, TextField } from "@mui/material";

interface AutocompleteBoxProps {
  lblString: string;
  fieldString: string;
  options: string[];
  labelWidth?: number | string; // mismo default que TextFieldBox
  width?: number | string; // ancho del campo (e.g. "50%", 420)
  maxWidth?: number | string; // límite opcional del campo
}

export default function AutocompleteBox({
  lblString,
  fieldString,
  options,
  labelWidth = 70,
  width,
  maxWidth,
}: AutocompleteBoxProps) {
  return (
    <Box sx={{ width: "100%", display: "flex", alignItems: "center", gap: 2 }}>
      {/* label fijo para alinear todo */}
      <Box
        component="label"
        htmlFor={lblString}
        sx={{ width: labelWidth, flexShrink: 0 }}
      >
        {lblString}
      </Box>

      {/* campo: ocupa width si lo pasas; si no, 100% */}
      <Autocomplete
        options={options}
        // solo pongo fullWidth cuando NO pasas `width`
        fullWidth={!width}
        sx={{
          ...(width ? { width } : { width: "100%" }),
          ...(maxWidth ? { maxWidth } : {}),
        }}
        renderInput={(params) => (
          <TextField {...params} label={`Select a ${fieldString}`} />
        )}
      />
    </Box>
  );
}
