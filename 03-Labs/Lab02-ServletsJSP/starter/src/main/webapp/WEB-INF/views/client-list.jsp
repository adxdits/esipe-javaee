<%-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. --%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${appName} - Clients</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Client Management</h1>
            <p class="subtitle">${appName}</p>
        </header>

        <c:if test="${not empty param.message}">
            <div class="alert alert-success">
                <c:choose>
                    <c:when test="${param.message == 'created'}">Client created successfully.</c:when>
                    <c:when test="${param.message == 'updated'}">Client updated successfully.</c:when>
                    <c:when test="${param.message == 'deleted'}">Client deleted successfully.</c:when>
                </c:choose>
            </div>
        </c:if>

        <form method="get" action="${pageContext.request.contextPath}/clients" class="search-form">
            <input type="text" name="search" value="<c:out value='${search}' />" placeholder="Search by name..." />
            <button type="submit">Search</button>
        </form>

        <!-- Client Table -->
        <table class="table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Accounts</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="client" items="${clients}">
                <tr>
                    <td>${client.id}</td>
                    <td><c:out value="${client.name}" /></td>
                    <td><c:out value="${client.email}" /></td>
                    <td>${client.accounts.size()}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/client?action=view&amp;id=${client.id}">View</a>
                        <a href="${pageContext.request.contextPath}/client?action=edit&amp;id=${client.id}">Edit</a>
                        <c:if test="${deletionEnabled}">
                            <form method="post" action="${pageContext.request.contextPath}/client" class="inline-form">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="${client.id}">
                                <button type="submit" onclick="return confirm('Delete this client?')">Delete</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
                </c:forEach>
                
                <c:if test="${empty clients}">
                    <tr><td colspan="5" class="no-data">No clients found.</td></tr>
                </c:if>
                
            </tbody>
        </table>

        <!-- Actions -->
        <div class="actions">
            <a href="${pageContext.request.contextPath}/client?action=new" class="btn btn-primary">New Client</a>
        </div>

        <footer>
            <p>
                Total Clients: ${clients.size()}
                <!-- TODO: Add more info if needed -->
            </p>
        </footer>
    </div>
</body>
</html>