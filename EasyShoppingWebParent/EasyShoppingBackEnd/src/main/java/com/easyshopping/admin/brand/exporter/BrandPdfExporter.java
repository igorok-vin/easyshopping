package com.easyshopping.admin.brand.exporter;

import com.easyshopping.admin.config.AbstractExporter;
import com.easyshopping.common.entity.Brand;
import com.easyshopping.common.entity.Category;
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

public class BrandPdfExporter extends AbstractExporter {

    public void export(List<Brand> brandList, HttpServletResponse response) throws IOException {
        setResponseHeader("brands_",response, "application/pdf", ".pdf");
        Document document = new Document(PageSize.LETTER);
        PdfWriter.getInstance(document,response.getOutputStream());
        document.open();

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(Color.BLUE);
        Paragraph paragraph = new Paragraph("List of brands",font);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph);
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100F);
        table.setSpacingBefore(10);
        table.setWidths(new float[] {1.2f, 3.5f, 3.5f});

        createTableHeader(table);

        createTableWithData(table, brandList);

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
        cell.setPhrase(new Phrase("Brand ID",font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Brand Name",font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Categories",font));
        table.addCell(cell);
    }

    /*Створюєм таблицю з даними і стилями*/
    private void createTableWithData(PdfPTable table, List<Brand> brandList) {
        for (Brand brand:brandList) {
            PdfPCell cell = new PdfPCell();
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            Font font = FontFactory.getFont(FontFactory.HELVETICA);
            font.setSize(9);

            cell.setPhrase(new Phrase(String.valueOf(brand.getId()),font));
            table.addCell(cell);

            cell.setPhrase(new Phrase(brand.getName(),font));
            table.addCell(cell);

            cell.setPhrase(new Phrase(brand.getCategories().toString(),font));
            table.addCell(cell);
        }
    }
}
