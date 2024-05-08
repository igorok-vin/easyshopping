package com.easyshopping.admin.config;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AbstractExporter {
    public void setResponseHeader(String entity,HttpServletResponse response,String contentType, String exportExtension) throws IOException {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_'at'_hh:mm:ss");
        String timeStamp = formatter.format(dateTime);

        String fileName = entity+timeStamp+exportExtension;

        response.setContentType(contentType);
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename="+fileName;
        response.setHeader(headerKey,headerValue);}
}
