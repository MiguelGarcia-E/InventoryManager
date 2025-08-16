import { Box } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";

interface DatePickerBoxProps {
  lblString: string;
  width?: string;
  labelWidth?: number | string;
}
export default function DatePickerBox({
  lblString,
  labelWidth = 70,
  width,
}: DatePickerBoxProps) {
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
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <DatePicker></DatePicker>
      </LocalizationProvider>
    </Box>
  );
}
