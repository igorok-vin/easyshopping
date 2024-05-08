package com.easyshopping.admin.brand.exporter;

import com.easyshopping.admin.config.AbstractExporter;
import com.easyshopping.common.entity.Brand;
import com.easyshopping.common.entity.Category;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class BrandCsvExporter extends AbstractExporter {
    public void export(List<Brand> brandsList, HttpServletResponse response) throws IOException {
        super.setResponseHeader("brands_",response,"text/csv",".csv");
        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String [] csvHeader = {"Brand ID","Brand Name", "Categories"};
        String [] fieldMapping = {"id","name","categories"};
        csvBeanWriter.writeHeader(csvHeader);
        for (Brand brand:brandsList) {
            csvBeanWriter.write(brand,fieldMapping);
        }
        csvBeanWriter.close();
    }
}
