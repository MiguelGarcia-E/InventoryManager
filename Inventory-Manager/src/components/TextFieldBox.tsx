import { Box, TextField } from "@mui/material";
interface TextFieldBoxProps {
  lblString: string;
  fieldString: string;
  width?: string;
  labelWidth?: number | string;
  value?: string | number;
  type?: React.HTMLInputTypeAttribute;
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
}
export default function TextFieldBox({
  lblString,
  fieldString,
  labelWidth = 75,
  width,
  value = "",
  type = "text",
  onChange,
}: TextFieldBoxProps) {
  const id = `tf-${lblString.replace(/\s+/g, "-").toLowerCase()}`;
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
        <label htmlFor={id}>{lblString}</label>{" "}
      </Box>{" "}
      <TextField
        id={id}
        label={fieldString}
        variant="outlined"
        type={type}
        sx={{ width: width || "100%" }}
        onChange={onChange}
        value={value ?? ""}
      ></TextField>{" "}
    </Box>
  );
}
