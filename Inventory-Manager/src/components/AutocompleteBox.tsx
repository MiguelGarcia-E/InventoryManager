import { Autocomplete, Box, TextField } from "@mui/material";

interface AutocompleteBoxProps {
  lblString: string; // label fijo
  fieldString: string; // label del campo
  options: string[]; // opciones del autocomplete
  labelWidth?: number | string; // ancho del label fijo
  width?: number | string; // ancho del campo
  maxWidth?: number | string; // max ancho del campo
}

export default function AutocompleteBox({
  lblString,
  fieldString,
  options,
  labelWidth = 75,
  width,
  maxWidth,
}: AutocompleteBoxProps) {
  return (
    <Box sx={{ width: "100%", display: "flex", alignItems: "center", gap: 2 }}>
      <Box
        component="label"
        htmlFor={lblString}
        sx={{ width: labelWidth, flexShrink: 0 }}
      >
        {lblString}
      </Box>

      <Autocomplete
        options={options}
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
