package com.bank.filter;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Logging Filter - Exercise 2
 * 
 * TODO: Implement logging filter that:
 * 1. Logs all HTTP requests with details (method, URI, parameters)
 * 2. Measures execution time for each request
 * 3. Logs response status code
 * 4. Provides performance monitoring
 * 
 * @author Your Name
 * @version 1.0
 */
@WebFilter(filterName = "LoggingFilter", urlPatterns = "/*")
public class LoggingFilter implements Filter {
    
    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("LoggingFilter initialized");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        long startTime = System.currentTimeMillis();
        logRequest(httpRequest);
        ResponseWrapper responseWrapper = new ResponseWrapper(httpResponse);
        
        try {
            chain.doFilter(request, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logResponse(httpRequest, responseWrapper, duration);
        }
    }
    
    @Override
    public void destroy() {
        LOGGER.info("LoggingFilter destroyed");
    }
    
    /**
     * TODO: Implement method to log incoming request details
     */
    private void logRequest(HttpServletRequest request) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n========== Incoming Request ==========\n");
        
        logMessage.append("Method: ").append(request.getMethod()).append('\n');
        logMessage.append("URI: ").append(request.getRequestURI()).append('\n');
        logMessage.append("Query String: ").append(request.getQueryString()).append('\n');
        logMessage.append("Remote Address: ").append(request.getRemoteAddr()).append('\n');
        
        // TODO: Log request parameters
        logMessage.append("Parameters:\n");
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String value = name.toLowerCase(Locale.ROOT).matches(".*(password|token|secret|authorization).*")
                    ? "[REDACTED]"
                    : request.getParameter(name);
            logMessage.append("  ").append(name).append(" = ").append(value).append('\n');
        }
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            logMessage.append("Session ID: ").append(session.getId()).append('\n');
        }
        
        logMessage.append("======================================");
        LOGGER.info(logMessage.toString());
    }
    
    /**
     * TODO: Implement method to log response details with timing
     */
    private void logResponse(HttpServletRequest request, ResponseWrapper response, long duration) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n========== Outgoing Response ==========\n");
        
        logMessage.append("Method: ").append(request.getMethod()).append('\n');
        logMessage.append("URI: ").append(request.getRequestURI()).append('\n');
        logMessage.append("Status Code: ").append(response.getStatus()).append('\n');
        logMessage.append("Content Type: ").append(response.getContentType()).append('\n');
        logMessage.append("Execution Time: ").append(duration).append(" ms\n");
        
        logMessage.append("=======================================");
        if (duration > 1000) {
            LOGGER.warning(logMessage.toString());
        } else {
            LOGGER.info(logMessage.toString());
        }
    }
    
    /**
     * Response wrapper to capture status code
     * TODO: Complete the implementation
     */
    private static class ResponseWrapper extends HttpServletResponseWrapper {
        private int status = HttpServletResponse.SC_OK;
        
        public ResponseWrapper(HttpServletResponse response) {
            super(response);
        }
        
        @Override
        public void setStatus(int sc) {
            status = sc;
            super.setStatus(sc);
        }
        
        @Override
        public void sendError(int sc) throws IOException {
            status = sc;
            super.sendError(sc);
        }
        
        @Override
        public void sendError(int sc, String msg) throws IOException {
            status = sc;
            super.sendError(sc, msg);
        }
        
        @Override
        public void sendRedirect(String location) throws IOException {
            status = HttpServletResponse.SC_FOUND;
            super.sendRedirect(location);
        }
        
        public int getStatus() {
            return status;
        }
    }
}

// Made with Bob
