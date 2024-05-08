package com.easyshopping.admin.serviceCommon;

import com.easyshopping.common.entity.Brand;
import com.easyshopping.common.entity.Category;
import com.easyshopping.common.entity.MainEntity;
import com.easyshopping.common.entity.User;
import com.easyshopping.common.entity.product.Product;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.util.List;

@Service
public class ServiceCommon {
    private String getURLParamValue(HttpServletRequest httpServletRequest, String paramName) throws URISyntaxException {
        String link = httpServletRequest.getHeader("referer").toString();
        List<NameValuePair> queryParams = new URIBuilder(link).getQueryParams();
        return queryParams.stream()
                .filter(param -> param.getName().equalsIgnoreCase(paramName))
                .map(NameValuePair::getValue)
                .findFirst()
                .orElse("");
    }

    public RedirectView getRedirectViewPostRequest(HttpServletRequest httpServletRequest, RedirectView redirect, String pageName, boolean checkID, MainEntity mainEntity) throws URISyntaxException {
        String currentPage = getURLParamValue(httpServletRequest, "page");
        String sortDirection = getURLParamValue(httpServletRequest, "sortDirection");
        String sortField = getURLParamValue(httpServletRequest, "sortField");
        String keyword = getURLParamValue(httpServletRequest, "keyword");
        String categoryId = getURLParamValue(httpServletRequest, "categoryId");

        if (checkID) {
            if (mainEntity instanceof Product) {
                if (currentPage == null & sortField == null & sortDirection == null & categoryId ==null & keyword == null) {
                    redirect.setUrl("/" + pageName);
                    return redirect;
                } else if (categoryId == null & keyword == null) {
                    redirect.setUrl("/" + pageName + "/page/" + currentPage + "?sortField=" + sortField + "&sortDirection=" + sortDirection);
                    return redirect;
                } else if (categoryId == null) {
                    redirect.setUrl("/" + pageName + "/page/" + currentPage + "?sortField=" + sortField + "&sortDirection=" + sortDirection + "&keyword=" + keyword);
                    return redirect;
                } else if (keyword == null) {
                    redirect.setUrl("/" + pageName + "/page/" + currentPage + "?sortField=" + sortField + "&sortDirection=" + sortDirection + "&categoryId=" + categoryId);
                    return redirect;
                } else {
                    redirect.setUrl("/" + pageName + "/page/" + currentPage + "?sortField=" + sortField + "&sortDirection=" + sortDirection + "&categoryId=" + categoryId + "&keyword=" + keyword);
                    return redirect;
                }
            } else if (currentPage == null & sortField == null & sortDirection == null & keyword == null) {
                redirect.setUrl("/" + pageName);
                return redirect;
            } else if (keyword == null) {
                redirect.setUrl("/" + pageName + "/page/" + currentPage + "?sortField=" + sortField + "&sortDirection=" + sortDirection);
                return redirect;
            } else {
                redirect.setUrl("/" + pageName + "/page/" + currentPage + "?sortField=" + sortField + "&sortDirection=" + sortDirection + "&keyword=" + keyword);
                return redirect;
            }
        } else if (mainEntity instanceof Product) {
            String productName = ((Product) mainEntity).getName();
            redirect.setUrl("/" + pageName + "/page/1?sortField=id&sortDirection=asc&keyword=" + productName);
            return redirect;
        } else if (mainEntity instanceof User) {
            String firstPartOfEmail = ((User) mainEntity).getEmail().split("@")[0];
            redirect.setUrl("/" + pageName + "/page/1?sortField=id&sortDirection=asc&keyword=" + firstPartOfEmail);
            return redirect;
        } else if (mainEntity instanceof Category) {
            String categoryName = ((Category) mainEntity).getName();
            redirect.setUrl("/" + pageName + "/page/1?sortField=id&sortDirection=asc&keyword=" + categoryName);
            return redirect;
        } else if (mainEntity instanceof Brand) {
            String brandName = ((Brand) mainEntity).getName();
            redirect.setUrl("/" + pageName + "/page/1?sortField=id&sortDirection=asc&keyword=" + brandName);
            return redirect;
        }
        return null;
    }

    public RedirectView getRedirectViewGetRequest(HttpServletRequest httpServletRequest, RedirectView redirect, String pageName) throws URISyntaxException {

        Integer currentPage = Integer.parseInt(httpServletRequest.getParameter("page"));
        String sortField = httpServletRequest.getParameter("sortField");
        String sortDirection = httpServletRequest.getParameter("sortDirection");
        String keyword = httpServletRequest.getParameter("keyword");

        if (currentPage == null & sortField == null & sortDirection == null & keyword == null) {
            redirect.setUrl("/" + pageName);
            return redirect;
        } else if (keyword == null) {
            redirect.setUrl("/" + pageName + "/page/" + currentPage + "?sortField=" + sortField + "&sortDirection=" + sortDirection);
            return redirect;
        } else {
            redirect.setUrl("/" + pageName + "/page/" + currentPage + "?sortField=" + sortField + "&sortDirection=" + sortDirection + "&keyword=" + keyword);
            return redirect;
        }
    }
}
