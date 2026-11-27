<%-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. --%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${appName} - ${empty client ? 'New' : 'Edit'} Client</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>${empty client ? 'New' : 'Edit'} Client</h1>
        </header>

        <c:if test="${not empty param.error}">
            <div class="alert alert-danger">
                <c:choose>
                    <c:when test="${param.error == 'name_required'}">Name is required.</c:when>
                    <c:when test="${param.error == 'email_required'}">Email is required.</c:when>
                    <c:otherwise>Please check the submitted information.</c:otherwise>
                </c:choose>
            </div>
        </c:if>

        <!-- Client Form -->
        <form method="post" action="${pageContext.request.contextPath}/client" class="form">
            
            <input type="hidden" name="action" value="${empty client ? 'create' : 'update'}">

            <c:if test="${not empty client}">
                <input type="hidden" name="id" value="${client.id}">
            </c:if>

            <!-- Name Field -->
            <div class="form-group">
                <label for="name">Name: <span class="required">*</span></label>
                <input type="text" id="name" name="name" value="<c:out value='${client.name}' />"
                       required maxlength="100">
            </div>

            <!-- Email Field -->
            <div class="form-group">
                <label for="email">Email: <span class="required">*</span></label>
                <input type="email" id="email" name="email" value="<c:out value='${client.email}' />"
                       required maxlength="254">
            </div>

            <!-- Form Actions -->
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">
                    ${empty client ? 'Create Client' : 'Update Client'}
                </button>

                <a href="${pageContext.request.contextPath}/clients" class="btn btn-secondary">Cancel</a>
            </div>
        </form>

        <!-- Help Text -->
        <div class="help-text">
            <p><span class="required">*</span> Required fields</p>
        </div>
    </div>
</body>
</html>