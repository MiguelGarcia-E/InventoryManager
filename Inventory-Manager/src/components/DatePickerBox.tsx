import { Box } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import type { Dayjs } from "dayjs";
import dayjs from "dayjs";

interface DatePickerBoxProps {
  lblString: string;
  width?: string;
  labelWidth?: number | string;
  value?: string | null;
  onChange?: (iso: string | null) => void;
  minToday?: boolean;
}
export default function DatePickerBox({
  lblString,
  labelWidth = 70,
  value = "",
  onChange,
  minToday = true,
}: DatePickerBoxProps) {
  const id = `dp-${lblString.replace(/\s+/g, "-").toLowerCase()}`;

  const dValue: Dayjs | null = value ? dayjs(value) : null;
  const minDate = minToday ? dayjs().add(1, "day").startOf("day") : undefined;
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
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <DatePicker
          value={dValue}
          onChange={(newValue) => {
            onChange?.(newValue ? newValue.format("YYYY-MM-DD") : null);
          }}
          format="YYYY-MM-DD"
          minDate={minDate}
        ></DatePicker>
      </LocalizationProvider>
    </Box>
  );
}
