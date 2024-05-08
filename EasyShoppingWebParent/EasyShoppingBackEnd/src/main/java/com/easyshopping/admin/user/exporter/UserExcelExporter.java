package com.easyshopping.admin.user.exporter;

import com.easyshopping.admin.config.AbstractExporter;
import com.easyshopping.common.entity.User;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class UserExcelExporter extends AbstractExporter {

    private XSSFWorkbook workbook;

    private XSSFSheet sheet;

    public UserExcelExporter() {
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("User");
        XSSFRow row = sheet.createRow(0);

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        cellStyle.setFont(font);

        createCell(row, 0, "User Id", cellStyle);
        createCell(row, 1, "Email", cellStyle);
        createCell(row, 2, "First Name", cellStyle);
        createCell(row, 3, "Last Name", cellStyle);
        createCell(row, 4, "Roles", cellStyle);
        createCell(row, 5, "Enabled", cellStyle);
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

    private void writeDataLines(List<User> userList) {
        int rowIndex = 1;
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        cellStyle.setFont(font);

        for (User user : userList) {
            XSSFRow row = sheet.createRow(rowIndex++);
            int columnIdex = 0;

            createCell(row, columnIdex++, user.getId(), cellStyle);
            createCell(row, columnIdex++, user.getEmail(), cellStyle);
            createCell(row, columnIdex++, user.getFirstName(), cellStyle);
            createCell(row, columnIdex++, user.getLastName(), cellStyle);
            createCell(row, columnIdex++, user.getRoles().toString(), cellStyle);
            createCell(row, columnIdex++, user.isEnabled(), cellStyle);
        }
    }

    public void export(List<User> userList, HttpServletResponse response) throws IOException {
        super.setResponseHeader("users_",response, "application/octet-stream", ".xlsx");

        writeHeaderLine();
        writeDataLines(userList);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
