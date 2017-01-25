package io.swagger.api.impl;

import org.jboss.resteasy.plugins.server.netty.NettyHttpRequest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.NewCookie;
import java.util.UUID;

/**
 * @author Kenzan
 */
public interface CatalogEdgeSessionToken {

    void setDomain (String domain);

    void updateSessionToken(HttpServletRequest req);

    void updateSessionToken(NettyHttpRequest req);

    boolean isValidToken();

    NewCookie getSessionCookie(UUID sessionToken);

    String getTokenValue();

    void setTokenValue(String tokenValue);
}
