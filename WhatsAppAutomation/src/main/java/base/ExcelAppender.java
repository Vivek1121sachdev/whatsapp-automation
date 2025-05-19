package base;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class ExcelAppender {

    public static void appendToExcel(String keyvalue) throws IOException {
        FileInputStream inputStream = null;
        Workbook workbook;

        try {
            File file = new File("results.xlsx");

            // If file doesn't exist, create one
            if (!file.exists()) {
                workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Sheet1");
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("Timestamp");
                header.createCell(1).setCellValue("Test Result");
            } else {
                inputStream = new FileInputStream(file);
                workbook = new XSSFWorkbook(inputStream);
            }

            Sheet sheet = workbook.getSheet("Sheet1");
            if (sheet == null) {
                sheet = workbook.createSheet("Sheet1");
            }

            // Get the last row number
            int lastRowNum = sheet.getLastRowNum();
            Row newRow = sheet.createRow(lastRowNum + 1);

            newRow.createCell(0).setCellValue(java.time.LocalDateTime.now().toString());
            newRow.createCell(1).setCellValue(keyvalue);

            //inputStream.close(); // Close before writing

            FileOutputStream outputStream = new FileOutputStream("results.xlsx");
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();

            System.out.println("✔ Data added to Excel file successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            inputStream.close();
        }
    }
}
