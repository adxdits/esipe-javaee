<%-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. --%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${appName} - Client Details</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Client Details</h1>
        </header>

        <!-- Client Information -->
        <div class="client-info card">
            <h2>Personal Information</h2>
            
            <p><strong>ID:</strong> ${client.id}</p>
            <p><strong>Name:</strong> <c:out value="${client.name}" /></p>
            <p><strong>Email:</strong> <c:out value="${client.email}" /></p>

        </div>

        <!-- Accounts Section -->
        <div class="accounts-section card">
            <h2>Accounts</h2>
            
            <c:choose>
                <c:when test="${empty client.accounts}">
                    <p class="no-data">No accounts found for this client.</p>
                </c:when>
                <c:otherwise>
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Account Number</th>
                                <th>Type</th>
                                <th>Balance</th>
                            </tr>
                        </thead>
                        <tbody>
                    <c:forEach var="account" items="${client.accounts}">
                        <tr>
                            <td><c:out value="${account.number}" /></td>
                            <td><c:out value="${account.type}" /></td>
                            <td><fmt:formatNumber value="${account.balance}" type="currency" /></td>
                        </tr>
                    </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Actions -->
        <div class="actions">
            <a href="${pageContext.request.contextPath}/client?action=edit&amp;id=${client.id}"
               class="btn btn-primary">Edit</a>
            <c:if test="${deletionEnabled}">
                <form method="post" action="${pageContext.request.contextPath}/client" class="inline-form">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" value="${client.id}">
                    <button type="submit" class="btn btn-danger"
                            onclick="return confirm('Are you sure you want to delete this client?')">Delete</button>
                </form>
            </c:if>
            <a href="${pageContext.request.contextPath}/clients" class="btn btn-secondary">Back to List</a>
        </div>
    </div>
</body>
</html>