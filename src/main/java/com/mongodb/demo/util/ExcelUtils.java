package com.mongodb.demo.util;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtils {

    private ExcelUtils() {
    }

    public static List<Map<String, String>> readSheet(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传的 Excel 文件为空");
        }
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet == null) {
                return List.of();
            }
            DataFormatter formatter = new DataFormatter();
            List<String> headers = new ArrayList<>();
            Row headerRow = sheet.getRow(sheet.getFirstRowNum());
            if (headerRow == null) {
                return List.of();
            }
            headerRow.forEach(cell -> headers.add(formatter.formatCellValue(cell).trim()));

            List<Map<String, String>> rows = new ArrayList<>();
            for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                Map<String, String> data = new LinkedHashMap<>();
                boolean hasValue = false;
                for (int col = 0; col < headers.size(); col++) {
                    String header = headers.get(col);
                    String value = formatter.formatCellValue(row.getCell(col)).trim();
                    if (!value.isEmpty()) {
                        hasValue = true;
                    }
                    data.put(header, value);
                }
                if (hasValue) {
                    rows.add(data);
                }
            }
            return rows;
        } catch (IOException e) {
            throw new IllegalArgumentException("读取 Excel 文件失败: " + e.getMessage(), e);
        }
    }
}
