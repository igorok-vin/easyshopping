package com.easyshopping.admin.category.exporter;

import com.easyshopping.admin.config.AbstractExporter;
import com.easyshopping.common.entity.Category;
import com.easyshopping.common.entity.User;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class CategoryCsvExporter extends AbstractExporter {
    public void export(List<Category> categoryList, HttpServletResponse response) throws IOException {
        super.setResponseHeader("categories_",response,"text/csv",".csv");

        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        String [] csvHeader = {"ID","Category Name", "Alias","Enabled"};
        String [] fieldMapping = {"id","name","alias","enabled"};
        csvBeanWriter.writeHeader(csvHeader);
        for (Category category:categoryList) {
            category.setName(category.getName().replace("--","   "));
            csvBeanWriter.write(category,fieldMapping);
        }
        csvBeanWriter.close();
    }
}
