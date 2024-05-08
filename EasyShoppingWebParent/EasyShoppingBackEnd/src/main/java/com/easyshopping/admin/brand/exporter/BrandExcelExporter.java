package com.easyshopping.admin.brand.exporter;

import com.easyshopping.admin.config.AbstractExporter;
import com.easyshopping.common.entity.Brand;
import com.easyshopping.common.entity.Category;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class BrandExcelExporter extends AbstractExporter {

    private XSSFWorkbook workbook;

    private XSSFSheet sheet;

    public BrandExcelExporter() {
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        /*creating new sheets*/
        sheet = workbook.createSheet("Brand");
        /*Excel document is empty, so update the code to create new row from the sheet*/
        XSSFRow row = sheet.createRow(0);

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        cellStyle.setFont(font);

        createCell(row, 0, "Brand ID", cellStyle);
        createCell(row, 1, "Brand Name", cellStyle);
        createCell(row, 2, "Categories", cellStyle);
    }

    private void createCell(XSSFRow row, int columnIdex, Object value, CellStyle cellStyle) {
        XSSFCell cell = row.createCell(columnIdex);
        sheet.autoSizeColumn(columnIdex);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(cellStyle);
    }

    private void writeDataLines(List<Brand> brandList) {
        int rowIndex = 1;
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        cellStyle.setFont(font);

        for (Brand brand : brandList) {
            XSSFRow row = sheet.createRow(rowIndex++);
            int columnIdex = 0;

            createCell(row, columnIdex++, brand.getId(), cellStyle);
            createCell(row, columnIdex++, brand.getName(), cellStyle);
            createCell(row, columnIdex++, brand.getCategories().toString(), cellStyle);
        }
    }

    public void export(List<Brand> brandList, HttpServletResponse response) throws IOException {
        super.setResponseHeader("brands_",response, "application/octet-stream", ".xlsx");

        writeHeaderLine();
        writeDataLines(brandList);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
