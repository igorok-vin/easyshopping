package com.easyshopping.admin.category.exporter;

import com.easyshopping.admin.config.AbstractExporter;
import com.easyshopping.common.entity.Category;
import com.easyshopping.common.entity.User;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class CategoryExcelExporter extends AbstractExporter {

    private XSSFWorkbook workbook;

    private XSSFSheet sheet;

    public CategoryExcelExporter() {
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Category");
        XSSFRow row = sheet.createRow(0);

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        cellStyle.setFont(font);

        createCell(row, 0, "ID", cellStyle);
        createCell(row, 1, "Category Name", cellStyle);
        createCell(row, 2, "Alias", cellStyle);
        createCell(row, 3, "Enabled", cellStyle);
    }

    private void createCell(XSSFRow row, int columnIdex, Object value, CellStyle cellStyle) {
        XSSFCell cell = row.createCell(columnIdex);
        sheet.autoSizeColumn(columnIdex);//авто розмір колонки
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(cellStyle);
    }

    private void writeDataLines(List<Category> categoryList) {
        int rowIndex = 1;
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        cellStyle.setFont(font);

        for (Category category : categoryList) {
            XSSFRow row = sheet.createRow(rowIndex++);
            int columnIdex = 0;

            createCell(row, columnIdex++, category.getId(), cellStyle);
            createCell(row, columnIdex++, category.getName(), cellStyle);
            createCell(row, columnIdex++, category.getAlias(), cellStyle);
            createCell(row, columnIdex++, category.isEnabled(), cellStyle);
        }
    }

    public void export(List<Category> categoryList, HttpServletResponse response) throws IOException {
        super.setResponseHeader("categories_",response, "application/octet-stream", ".xlsx");

        writeHeaderLine();
        writeDataLines(categoryList);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
