package io.swagger.api.impl;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.NewCookie;
import java.util.UUID;

/**
 * @author Kenzan
 */
public interface CatalogEdgeSessionToken {

    void updateSessionToken(HttpServletRequest req);

    boolean isValidToken();

    NewCookie getSessionCookie(UUID sessionToken);

    String getTokenValue();

    void setTokenValue(String tokenValue);
}
