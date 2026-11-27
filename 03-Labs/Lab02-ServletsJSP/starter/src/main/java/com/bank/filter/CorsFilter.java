package com.bank.filter;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * CORS Filter - Exercise 4
 * 
 * TODO: Implement CORS filter that:
 * 1. Handles Cross-Origin Resource Sharing for REST APIs
 * 2. Configures CORS headers (Access-Control-Allow-*)
 * 3. Handles preflight requests (OPTIONS method)
 * 4. Validates allowed origins
 * 
 * @author Your Name
 * @version 1.0
 */
@WebFilter(filterName = "CorsFilter", urlPatterns = "/api/*")
public class CorsFilter implements Filter {
    
    private static final Logger LOGGER = Logger.getLogger(CorsFilter.class.getName());
    
    private static final String ALLOWED_ORIGINS =
            "http://localhost:3000,http://localhost:4200,http://localhost:8080";
    private static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
    private static final String ALLOWED_HEADERS =
            "Content-Type, Authorization, X-Requested-With, Accept, Origin";
    private static final String EXPOSED_HEADERS = "Location, Content-Disposition";
    private static final String MAX_AGE = "3600";
    private static final String ALLOW_CREDENTIALS = "true";
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("CorsFilter initialized");
        LOGGER.info("Allowed CORS origins: " + ALLOWED_ORIGINS);
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String origin = httpRequest.getHeader("Origin");
        String method = httpRequest.getMethod();
        
        LOGGER.fine("Processing CORS request - Origin: " + origin + ", Method: " + method);
        
        if (origin != null && isOriginAllowed(origin)) {
            httpResponse.setHeader("Access-Control-Allow-Origin", origin);
            httpResponse.setHeader("Access-Control-Allow-Methods", ALLOWED_METHODS);
            httpResponse.setHeader("Access-Control-Allow-Headers", ALLOWED_HEADERS);
            httpResponse.setHeader("Access-Control-Expose-Headers", EXPOSED_HEADERS);
            httpResponse.setHeader("Access-Control-Max-Age", MAX_AGE);
            httpResponse.setHeader("Access-Control-Allow-Credentials", ALLOW_CREDENTIALS);
            LOGGER.fine("CORS headers set for origin: " + origin);
        } else if (origin != null) {
            LOGGER.warning("Origin not allowed: " + origin);
        }
        
        if ("OPTIONS".equalsIgnoreCase(method)) {
            LOGGER.info("Handling preflight request from origin: " + origin);
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        LOGGER.info("CorsFilter destroyed");
    }
    
    /**
     * TODO: Implement method to check if origin is allowed
     */
    private boolean isOriginAllowed(String origin) {
        for (String allowed : ALLOWED_ORIGINS.split(",")) {
            if (allowed.trim().equals(origin)) {
                return true;
            }
        }
        return false;
    }
}

// Made with Bob
