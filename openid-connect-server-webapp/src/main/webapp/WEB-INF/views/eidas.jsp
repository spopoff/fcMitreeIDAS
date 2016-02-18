<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="o" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <form:form modelAttribute="eidasModel" method="POST" action="runeidas">
            <div>Eidas Node: ${eidasModel.nodeUrl}</div>
            <div>Return URL: ${eidasModel.returnUrl}</div>
            <div>
                <form:select path="citizen">
                    <c:forEach items="${eidasModel.countries}" var="citizen" varStatus="vs">
                        <form:option value="${citizen.name}">${citizen.name}</form:option>
                    </c:forEach>
                </form:select>
            </div>
            <div><input type="submit" class="btn" value="Run" name="submit"></div>
        </form:form>
    </body>
</html>
