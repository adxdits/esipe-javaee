package com.bank.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet(name = "WelcomeServlet", urlPatterns = {"/welcome"})
public class WelcomeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' HH:mm:ss");
        String formattedDateTime = LocalDateTime.now().format(formatter);

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang=\"en\">");
            out.println("<head>");
            out.println("  <meta charset=\"UTF-8\">");
            out.println("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
            out.println("  <title>Welcome - Banking Application</title>");
            out.println("  <link rel=\"stylesheet\" href=\"css/style.css\">");
            out.println("</head>");
            out.println("<body>");
            out.println("  <div class=\"container\">");
            out.println("    <h1>Welcome to Banking Application</h1>");
            out.println("    <p class=\"subtitle\">Your first Jakarta EE servlet</p>");
            out.println("    <div class=\"card text-center\">");
            out.println("      <h2>Hello!</h2>");
            out.println("      <p>Current Date and Time: " + formattedDateTime + "</p>");
            out.println("    </div>");
            out.println("    <nav class=\"mt-20\">");
            out.println("      <ul>");
            out.println("        <li><a href=\"index.html\">Home</a></li>");
            out.println("        <li><a href=\"clients\">View Clients</a></li>");
            out.println("        <li><a href=\"add-client.html\">Add New Client</a></li>");
            out.println("      </ul>");
            out.println("    </nav>");
            out.println("  </div>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    public String getServletInfo() {
        return "Welcome page servlet for the banking application";
    }
}
