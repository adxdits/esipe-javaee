package com.bank.listener;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Session Counter Listener - Exercise 3
 * 
 * TODO: Implement session listener that:
 * 1. Tracks active user sessions
 * 2. Counts total sessions created
 * 3. Logs session lifecycle events
 * 4. Stores statistics in ServletContext
 * 
 * @author Your Name
 * @version 1.0
 */
@WebListener
public class SessionCounterListener implements HttpSessionListener, HttpSessionAttributeListener {
    
    private static final Logger LOGGER = Logger.getLogger(SessionCounterListener.class.getName());
    
    // TODO: Create thread-safe counters using AtomicInteger
    private static final AtomicInteger activeSessions = new AtomicInteger();
    private static final AtomicInteger totalSessions = new AtomicInteger();
    
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        
        int active = activeSessions.incrementAndGet();
        int total = totalSessions.incrementAndGet();
        se.getSession().getServletContext().setAttribute("activeSessions", active);
        se.getSession().getServletContext().setAttribute("totalSessions", total);
        session.setMaxInactiveInterval(30 * 60);
        session.setAttribute("creationTime", session.getCreationTime());
        LOGGER.info("Session created: " + session.getId() + " | Active: " + active + " | Total: " + total);
    }
    
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        
        int active = activeSessions.decrementAndGet();
        session.getServletContext().setAttribute("activeSessions", active);
        Object value = session.getAttribute("creationTime");
        long creationTime = value instanceof Number
                ? ((Number) value).longValue()
                : session.getCreationTime();
        long duration = Math.max(0, (System.currentTimeMillis() - creationTime) / 1000);
        LOGGER.info("Session destroyed: " + session.getId() + " | Active: " + active
                + " | Duration: " + duration + " seconds");
        if (session.getAttribute("user") != null) {
            LOGGER.info("User logged out: " + session.getAttribute("user"));
        }
    }
    
    @Override
    public void attributeAdded(HttpSessionBindingEvent se) {
        String name = se.getName();
        Object value = se.getValue();
        
        if ("user".equals(name)) {
            LOGGER.info("User logged in: " + value);
        }
        LOGGER.fine("Session attribute added: " + name);
    }
    
    @Override
    public void attributeRemoved(HttpSessionBindingEvent se) {
        String name = se.getName();
        Object value = se.getValue();
        
        if ("user".equals(name)) {
            LOGGER.info("User logged out: " + value);
        }
        LOGGER.fine("Session attribute removed: " + name);
    }
    
    @Override
    public void attributeReplaced(HttpSessionBindingEvent se) {
        String name = se.getName();
        Object oldValue = se.getValue();
        Object newValue = se.getSession().getAttribute(name);
        
        if ("user".equals(name)) {
            LOGGER.info("User changed from " + oldValue + " to " + newValue);
        }
        LOGGER.fine("Session attribute replaced: " + name);
    }
    
    /**
     * TODO: Implement method to get the number of active sessions
     */
    public static int getActiveSessions() {
        return activeSessions.get();
    }
    
    /**
     * TODO: Implement method to get the total number of sessions created
     */
    public static int getTotalSessions() {
        return totalSessions.get();
    }
    
    /**
     * TODO: Implement method to reset counters (for testing)
     */
    public static void resetCounters() {
        activeSessions.set(0);
        totalSessions.set(0);
        LOGGER.info("Session counters reset");
    }
}

// Made with Bob
