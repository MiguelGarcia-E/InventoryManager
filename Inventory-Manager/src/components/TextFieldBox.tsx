import { Box, TextField } from "@mui/material";
interface TextFieldBoxProps {
  lblString: string;
  fieldString: string;
  width?: string;
  labelWidth?: number | string;
}
export default function TextFieldBox({
  lblString,
  fieldString,
  labelWidth = 75,
  width,
}: TextFieldBoxProps) {
  return (
    <Box
      sx={{
        width: "100%",
        display: "flex",
        flexDirection: "row",
        gap: 2,
        alignItems: "center",
      }}
    >
      {" "}
      <Box sx={{ width: labelWidth }}>
        {" "}
        <label htmlFor={lblString}>{lblString}</label>{" "}
      </Box>{" "}
      <TextField
        label={fieldString}
        variant="outlined"
        sx={{ width: width || "100%" }}
      ></TextField>{" "}
    </Box>
  );
}
