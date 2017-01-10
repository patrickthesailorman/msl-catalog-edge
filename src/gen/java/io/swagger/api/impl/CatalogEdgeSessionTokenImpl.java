package io.swagger.api.impl;

import org.jboss.resteasy.plugins.server.netty.NettyHttpRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.NewCookie;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class CatalogEdgeSessionTokenImpl implements CatalogEdgeSessionToken{

    private String tokenValue;
    private String domain = "localhost";

    public void setDomain (String domain) {
        this.domain = domain;
    }

    /**
     * Updates sessionToken on each coming request
     *
     * @param req HttpServletRequest
     * @deprecated only used when running the local catalog edge
     * module which uses the catalogEdgeApiOriginFilter original approach
     *
     */
    public void updateSessionToken(HttpServletRequest req) {
        String sessionTokenValue = "";
        if (req.getCookies() != null) {
            Cookie[] cookies = req.getCookies();
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals("sessionToken")) {
                    sessionTokenValue = cookies[i].getValue();
                }
            }
        }
        setTokenValue(sessionTokenValue);
    }

    /**
     * Updates sessionToken from incoming netty http request
     *
     * @param req NettyHttpRequest
     */
    public void updateSessionToken (NettyHttpRequest req) {
        String sessionTokenValue = "";
        if (req.getHttpHeaders().getCookies() != null) {
            Map<String, javax.ws.rs.core.Cookie> cookies = req.getHttpHeaders().getCookies();
            for (Map.Entry<String, javax.ws.rs.core.Cookie> entry : cookies.entrySet()) {
                if (entry.getValue().getName().equals("sessionToken")) {
                    sessionTokenValue = entry.getValue().getName();
                }
            }
        }
        setTokenValue(sessionTokenValue);
    }

    /**
     * Checks whether or not the token is a valid UUID
     *
     * @return boolean
     */
    public boolean isValidToken() {
        try {
            UUID.fromString(getTokenValue());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Returns the sessionToken in a cookie format, if a sessionToken is passed it updates the CatalogEdgeSessionTokenImpl value with it
     *
     * @param sessionToken value to update CatalogEdgeSessionTokenImpl.value with
     * @return NewCookie
     */
    public NewCookie getSessionCookie(UUID sessionToken) {

        final String PATH = "/";
        final String DOMAIN = "local.msl.dev";
        final int VERSION = 1;
        final String COMMENT = "";
        int MAX_AGE;

        if (null == sessionToken) {
            setTokenValue("");
            MAX_AGE = 0;
        } else {
            setTokenValue(sessionToken.toString());
            MAX_AGE = 24 * 60 * 60;
        }

        return new NewCookie(
                "sessionToken",
                getTokenValue(),
                PATH,
                DOMAIN,
                VERSION,
                COMMENT,
                MAX_AGE,
                getExpirationDate(),
                false,
                true
        );
    }

    /**
     * Returns an expiration date of a day
     *
     * @return Date
     */
    private Date getExpirationDate () {
        Date today = new Date();
        return new Date(today.getTime() + (1000 * 60 * 60 * 24));
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }
}
