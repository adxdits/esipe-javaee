package com.bank.filter;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Authentication Filter - Exercise 1
 * 
 * Redirects unauthenticated requests to protected URL prefixes to the login page.
 * 
 * @author Your Name
 * @version 1.0
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = "/*")
public class AuthenticationFilter implements Filter {
    
    private static final Logger LOGGER = Logger.getLogger(AuthenticationFilter.class.getName());
    
    private static final List<String> PUBLIC_URLS = Arrays.asList(
        "/login", "/logout", "/public/", "/css/", "/js/", "/images/"
    );
    
    private static final List<String> PROTECTED_URLS = Arrays.asList(
        "/clients/", "/admin/"
    );
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO: Initialize the filter
        LOGGER.info("AuthenticationFilter initialized");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());
        
        LOGGER.fine("Processing authentication for path: " + path);
        
        if (isPublicUrl(path)) {
            LOGGER.fine("Public URL, allowing access: " + path);
            chain.doFilter(request, response);
            return;
        }
        
        if (isProtectedUrl(path)) {
            HttpSession session = httpRequest.getSession(false);
            
            if (session == null || session.getAttribute("user") == null) {
                LOGGER.warning("Unauthorized access attempt to: " + path);
                httpRequest.getSession(true).setAttribute("originalUrl", requestURI);
                httpResponse.sendRedirect(contextPath + "/login");
                return;
            }
            
            String username = String.valueOf(session.getAttribute("user"));
            LOGGER.info("Authenticated user '" + username + "' accessing: " + path);
        }
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        LOGGER.info("AuthenticationFilter destroyed");
    }
    
    /**
     * TODO: Implement method to check if the URL is public
     */
    private boolean isPublicUrl(String path) {
        return PUBLIC_URLS.stream().anyMatch(path::startsWith);
    }
    
    /**
     * TODO: Implement method to check if the URL is protected
     */
    private boolean isProtectedUrl(String path) {
        return PROTECTED_URLS.stream().anyMatch(path::startsWith);
    }
}

// Made with Bob
