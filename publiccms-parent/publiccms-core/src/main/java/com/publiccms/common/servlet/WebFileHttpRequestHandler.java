package com.publiccms.common.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.util.UrlPathHelper;

import com.publiccms.common.constants.CmsVersion;
import com.publiccms.common.constants.CommonConstants;
import com.publiccms.entities.sys.SysSite;
import com.publiccms.logic.component.site.SiteComponent;

/**
 *
 * MultiSiteWebHttpRequestHandler 多站点静态资源处理器
 * 
 */
public class WebFileHttpRequestHandler extends ResourceHttpRequestHandler {
    private UrlPathHelper urlPathHelper = new UrlPathHelper();
    private SiteComponent siteComponent;

    /**
     * @param siteComponent
     */
    public WebFileHttpRequestHandler(SiteComponent siteComponent) {
        this.siteComponent = siteComponent;
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader(CommonConstants.getXPowered(), CmsVersion.getVersion());
        super.handleRequest(request, response);

    }

    @Override
    protected void setHeaders(HttpServletResponse response, Resource resource, @Nullable MediaType mediaType) throws IOException {
        super.setHeaders(response, resource, mediaType);
    }

    @Override
    protected Resource getResource(HttpServletRequest request) throws IOException {
        String path = urlPathHelper.getLookupPathForRequest(request);
        if (path.endsWith(CommonConstants.SEPARATOR)) {
            path += CommonConstants.getDefaultPage();
        }
        SysSite site = siteComponent.getSite(request.getServerName());
        Resource resource = new FileSystemResource(siteComponent.getWebFilePath(site, path));
        if (resource.exists()) {
            if (resource.isReadable()) {
                return resource;
            }
        } else if (null != site.getParentId()) {
            resource = new FileSystemResource(siteComponent.getParentSiteWebFilePath(site, path));
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
        }
        return null;
    }
}
