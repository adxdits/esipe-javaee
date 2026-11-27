package com.bank.web;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.model.Client;
import com.bank.model.Account;
import com.bank.service.ClientService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller servlet for managing clients.
 * Handles all client-related HTTP requests.
 * Uses JavaBeans pattern (no CDI).
 *
 * Front controller for client management.
 */
@WebServlet(
    name = "ClientController",
    urlPatterns = {"/clients", "/client"},
    loadOnStartup = 1
)
public class ClientController extends HttpServlet {
    
    private ClientService clientService;
    
    // Configuration properties (read from ServletContext init parameters)
    private int defaultPageSize;
    private boolean deletionEnabled;
    private String appName;
    
    /**
     * Initialize the servlet.
     *
     * TODO: Implement this method
     * Steps:
     * 1. Create ClientService instance (JavaBean)
     * 2. Read configuration from ServletContext init parameters
     * 3. Add some sample data for testing
     * 4. Log initialization message
     */
    @Override
    public void init() throws ServletException {
        super.init();
        
        clientService = new ClientService();
        String pageSize = getServletContext().getInitParameter("web.pagination.default.size");
        try {
            defaultPageSize = pageSize == null ? 10 : Integer.parseInt(pageSize);
        } catch (NumberFormatException e) {
            throw new ServletException("Invalid web.pagination.default.size: " + pageSize, e);
        }
        String deletion = getServletContext().getInitParameter("feature.client.deletion.enabled");
        deletionEnabled = deletion == null || Boolean.parseBoolean(deletion);
        appName = getServletContext().getInitParameter("app.name");
        if (appName == null || appName.trim().isEmpty()) {
            appName = "Banking Application";
        }
        clientService.initializeSampleData();
        log("ClientController initialized successfully");
    }
    
    /**
     * Handle GET requests.
     * Routes to appropriate handler based on action parameter.
     * 
     * TODO: Implement routing logic
     * Actions:
     * - null or "list": show client list
     * - "view": show client details
     * - "new": show empty form
     * - "edit": show form with client data
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String action = req.getParameter("action");
        
        if ("/clients".equals(req.getServletPath())) {
            listClients(req, resp);
            return;
        }
        if (action == null || action.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/clients");
            return;
        }
        switch (action) {
            case "list":
                listClients(req, resp);
                break;
            case "view":
                viewClient(req, resp);
                break;
            case "new":
            case "edit":
                showForm(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown client action");
        }
    }
    
    /**
     * Handle POST requests.
     * Processes form submissions.
     * 
     * TODO: Implement form handling
     * Actions:
     * - "create": create new client
     * - "update": update existing client
     * - "delete": delete client
     * Use PRG pattern (Post-Redirect-Get)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String action = req.getParameter("action");
        
        req.setCharacterEncoding("UTF-8");
        if (action == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing action");
            return;
        }
        switch (action) {
            case "create":
                createClient(req, resp);
                break;
            case "update":
                updateClient(req, resp);
                break;
            case "delete":
                deleteClient(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown client action");
        }
    }
    
    /**
     * Display list of all clients.
     * 
     * TODO: Implement this method
     * Steps:
     * 1. Get all clients from service
     * 2. Set clients as request attribute
     * 3. Set other attributes (appName, deletionEnabled)
     * 4. Forward to client-list.jsp
     */
    private void listClients(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String search = req.getParameter("search");
        req.setAttribute("clients",
                search == null || search.trim().isEmpty()
                        ? clientService.findAll()
                        : clientService.findByName(search));
        req.setAttribute("search", search == null ? "" : search);
        setViewAttributes(req);
        req.getRequestDispatcher("/WEB-INF/views/client-list.jsp").forward(req, resp);
    }
    
    /**
     * Display details of a specific client.
     * 
     * TODO: Implement this method
     * Steps:
     * 1. Get id parameter
     * 2. Find client by ID
     * 3. If not found, send 404 error
     * 4. Set client as request attribute
     * 5. Forward to client-details.jsp
     */
    private void viewClient(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        Long id = parseId(req, resp);
        if (id == null) {
            return;
        }
        Client client = clientService.findById(id);
        if (client == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Client not found");
            return;
        }
        req.setAttribute("client", client);
        setViewAttributes(req);
        req.getRequestDispatcher("/WEB-INF/views/client-details.jsp").forward(req, resp);
    }
    
    /**
     * Show form for creating/editing client.
     * 
     * TODO: Implement this method
     * Steps:
     * 1. If editing, get id and find client
     * 2. Set client as attribute (null for new)
     * 3. Forward to client-form.jsp
     */
    private void showForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String action = req.getParameter("action");
        
        Client client = null;
        if ("edit".equals(action)) {
            Long id = parseId(req, resp);
            if (id == null) {
                return;
            }
            client = clientService.findById(id);
            if (client == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Client not found");
                return;
            }
        }
        req.setAttribute("client", client);
        setViewAttributes(req);
        req.getRequestDispatcher("/WEB-INF/views/client-form.jsp").forward(req, resp);
    }
    
    /**
     * Create a new client.
     * 
     * TODO: Implement this method
     * Steps:
     * 1. Get form parameters (name, email)
     * 2. Validate input
     * 3. Create Client object
     * 4. Save using service
     * 5. Redirect to list with success message (PRG pattern)
     */
    private void createClient(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        
        String name = trimToNull(req.getParameter("name"));
        String email = trimToNull(req.getParameter("email"));
        if (name == null) {
            redirectWithError(req, resp, "name_required");
            return;
        }
        if (email == null) {
            redirectWithError(req, resp, "email_required");
            return;
        }
        clientService.create(new Client(name, email));
        resp.sendRedirect(req.getContextPath() + "/clients?message=created");
    }
    
    /**
     * Update an existing client.
     * 
     * TODO: Implement this method
     * Similar to createClient but:
     * 1. Get id parameter
     * 2. Find existing client
     * 3. Update fields
     * 4. Save using service
     * 5. Redirect to list
     */
    private void updateClient(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        
        String name = trimToNull(req.getParameter("name"));
        String email = trimToNull(req.getParameter("email"));
        if (name == null) {
            redirectWithError(req, resp, "name_required");
            return;
        }
        if (email == null) {
            redirectWithError(req, resp, "email_required");
            return;
        }
        Long id = parseId(req, resp);
        if (id == null) {
            return;
        }
        Client client = clientService.findById(id);
        if (client == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Client not found");
            return;
        }
        client.setName(name);
        client.setEmail(email);
        clientService.update(client);
        resp.sendRedirect(req.getContextPath() + "/clients?message=updated");
    }
    
    /**
     * Delete a client.
     * 
     * TODO: Implement this method
     * Steps:
     * 1. Check if deletion is enabled (deletionEnabled)
     * 2. Get id parameter
     * 3. Delete using service
     * 4. Redirect to list
     */
    private void deleteClient(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        
        if (!deletionEnabled) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Client deletion is disabled");
            return;
        }
        Long id = parseId(req, resp);
        if (id == null) {
            return;
        }
        if (!clientService.delete(id)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Client not found");
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/clients?message=deleted");
    }

    private Long parseId(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String value = req.getParameter("id");
        try {
            if (value == null || value.trim().isEmpty()) {
                throw new NumberFormatException("missing id");
            }
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid client id");
            return null;
        }
    }

    private void setViewAttributes(HttpServletRequest req) {
        req.setAttribute("appName", appName);
        req.setAttribute("deletionEnabled", deletionEnabled);
        req.setAttribute("defaultPageSize", defaultPageSize);
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private void redirectWithError(HttpServletRequest req, HttpServletResponse resp, String error)
            throws IOException {
        resp.sendRedirect(req.getContextPath() + "/client?action=new&error=" + error);
    }
    
    /**
     * Cleanup resources.
     */
    @Override
    public void destroy() {
        log("ClientController destroyed");
        super.destroy();
    }
}

// Made with Bob
