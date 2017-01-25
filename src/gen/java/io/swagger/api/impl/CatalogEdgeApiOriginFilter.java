package io.swagger.api.impl;

import com.google.inject.Inject;
import com.kenzan.msl.common.utils.Utils;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CatalogEdgeApiOriginFilter implements javax.servlet.Filter {

    private DynamicStringProperty CLIENT_PORT =
            DynamicPropertyFactory.getInstance().getStringProperty("clientPort", "3000");

    @Inject
    private static CatalogEdgeSessionToken catalogEdgeSessionToken;

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        res.addHeader("Access-Control-Allow-Origin", Utils.getHost(req.getRequestURL().toString(), CLIENT_PORT.get()));
        res.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        res.addHeader("Access-Control-Allow-Headers", "Content-Type");
        res.addHeader("Access-Control-Allow-Credentials", "true");
        catalogEdgeSessionToken.setDomain(req.getServerName());
        catalogEdgeSessionToken.updateSessionToken(req);
        chain.doFilter(request, response);
    }

    public void destroy() {}

    public void init(FilterConfig filterConfig) throws ServletException {}
}
