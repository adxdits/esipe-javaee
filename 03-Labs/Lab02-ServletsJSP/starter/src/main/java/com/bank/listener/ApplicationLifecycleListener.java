package com.bank.listener;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * Application Lifecycle Listener
 * 
 * TODO: Implement application lifecycle listener that:
 * 1. Monitors application startup and shutdown
 * 2. Initializes application configuration
 * 3. Stores application metadata in ServletContext
 * 4. Tracks application uptime
 * 5. Performs cleanup on shutdown
 * 
 * @author Your Name
 * @version 1.0
 */
@WebListener
public class ApplicationLifecycleListener implements ServletContextListener {
    
    private static final Logger LOGGER = Logger.getLogger(ApplicationLifecycleListener.class.getName());
    
    // TODO: Create date formatter for logging
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        
        // TODO: Record startup time (System.currentTimeMillis())
        long startupTime = System.currentTimeMillis();
        LocalDateTime startupDateTime = LocalDateTime.now();
        context.setAttribute("startupTime", startupTime);
        context.setAttribute("startupDateTime", DATE_FORMATTER.format(startupDateTime));

        String appName = context.getServletContextName();
        if (appName == null || appName.isEmpty()) {
            appName = "Banking Web Application";
        }
        String contextPath = context.getContextPath();
        int majorVersion = context.getMajorVersion();
        int minorVersion = context.getMinorVersion();
        context.setAttribute("appName", appName);
        context.setAttribute("appVersion", "1.0.0");
        context.setAttribute("servletVersion", majorVersion + "." + minorVersion);
        context.setAttribute("contextPath", contextPath);
        context.setAttribute("activeSessions", 0);
        context.setAttribute("totalSessions", 0);
        
        // TODO: Log startup information
        LOGGER.info("========================================");
        LOGGER.info("APPLICATION STARTUP");
        LOGGER.info("========================================");
        LOGGER.info("Application: " + appName);
        LOGGER.info("Context path: " + contextPath);
        LOGGER.info("Servlet Version: " + majorVersion + "." + minorVersion);
        LOGGER.info("Started at: " + DATE_FORMATTER.format(startupDateTime));
        LOGGER.info("========================================");
        
        // TODO: Initialize application resources
        initializeResources(context);
        
        LOGGER.info("Application initialized successfully");
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        
        // TODO: Calculate uptime
        Object startValue = context.getAttribute("startupTime");
        long startupTime = startValue instanceof Number ? ((Number) startValue).longValue() : System.currentTimeMillis();
        long uptime = Math.max(0, (System.currentTimeMillis() - startupTime) / 1000);
        Object activeValue = context.getAttribute("activeSessions");
        Object totalValue = context.getAttribute("totalSessions");
        int activeSessions = activeValue instanceof Number ? ((Number) activeValue).intValue() : 0;
        int totalSessions = totalValue instanceof Number ? ((Number) totalValue).intValue() : 0;
        
        // TODO: Log shutdown information
        LOGGER.info("========================================");
        LOGGER.info("APPLICATION SHUTDOWN");
        LOGGER.info("========================================");
        LOGGER.info("Uptime: " + formatUptime(uptime));
        LOGGER.info("Active sessions: " + activeSessions + " | Total sessions: " + totalSessions);
        LOGGER.info("========================================");
        
        // TODO: Cleanup resources
        cleanupResources(context);
        
        LOGGER.info("Application shutdown complete");
    }
    
    /**
     * TODO: Implement method to initialize application resources
     */
    private void initializeResources(ServletContext context) {
        LOGGER.info("Initializing application resources...");
        
        // TODO: Initialize any required resources
        // - Database connections
        // - Thread pools
        // - Caches
        // - Configuration
        
        LOGGER.info("Application resources initialized");
    }
    
    /**
     * TODO: Implement method to cleanup application resources
     */
    private void cleanupResources(ServletContext context) {
        LOGGER.info("Cleaning up application resources...");
        
        // TODO: Cleanup resources
        // - Close database connections
        // - Shutdown thread pools
        // - Clear caches
        
        context.removeAttribute("startupTime");
        context.removeAttribute("startupDateTime");
        context.removeAttribute("appName");
        context.removeAttribute("appVersion");
        context.removeAttribute("servletVersion");
        context.removeAttribute("contextPath");
        
        LOGGER.info("Application resources cleaned up");
    }
    
    /**
     * TODO: Implement method to format uptime in human-readable format
     */
    private String formatUptime(long seconds) {
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;
        return days + " days, " + hours + " hours, " + minutes + " minutes, "
                + remainingSeconds + " seconds";
    }
}

// Made with Bob
