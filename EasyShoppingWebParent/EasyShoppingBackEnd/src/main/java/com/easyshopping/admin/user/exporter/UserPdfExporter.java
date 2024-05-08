package com.easyshopping.admin.user.exporter;

import com.easyshopping.admin.config.AbstractExporter;
import com.easyshopping.common.entity.User;
import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class UserPdfExporter extends AbstractExporter {

    public void export(List<User> userList, HttpServletResponse response) throws IOException {
        setResponseHeader("users_",response, "application/pdf", ".pdf");
        Document document =new Document(PageSize.LETTER);
        PdfWriter.getInstance(document,response.getOutputStream());
        document.open();

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(Color.BLUE);
        Paragraph paragraph = new Paragraph("List of users",font);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100F);
        table.setSpacingBefore(10);
        table.setWidths(new float[] {1.2f, 3.5f, 3.0f, 3.0f, 3.0f, 1.7f});

        createTableHeader(table);

        createTableWithData(table, userList);

        document.add(table);

        document.close();

        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.close();
    }

    private void createTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.BLUE);
        cell.setPadding(5);
        cell.setHorizontalAlignment(1);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setSize(11);
        font.setColor(Color.WHITE);
        cell.setPhrase(new Phrase("ID",font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Email",font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("First Name",font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Last Name",font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Roles",font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Enabled",font));
        table.addCell(cell);
    }

    private void createTableWithData(PdfPTable table, List<User> userList) {
        for (User user:userList
        ) {
            PdfPCell cell = new PdfPCell();
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            Font font = FontFactory.getFont(FontFactory.HELVETICA);
            font.setSize(9);

            cell.setPhrase(new Phrase(String.valueOf(user.getId()),font));
            table.addCell(cell);

            cell.setPhrase(new Phrase(user.getEmail(),font));
            table.addCell(cell);

            cell.setPhrase(new Phrase(user.getFirstName(),font));
            table.addCell(cell);

            cell.setPhrase(new Phrase(user.getLastName(),font));
            table.addCell(cell);

            cell.setPhrase(new Phrase(user.getRoles().toString(),font));
            table.addCell(cell);

            cell.setPhrase(new Phrase(String.valueOf(user.isEnabled()),font));
            table.addCell(cell);
        }
    }
}
