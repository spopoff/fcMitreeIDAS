<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html lang="en">

<body onload="document.redirectForm.submit();">
<form:form id="redirectForm" name="redirectForm" method="post" action="${eidasModel.nodeUrl}">
    <input type="hidden" id="SAMLRequest" name="SAMLRequest" value="${eidasModel.SAMLRequest}"/>
    <input type="hidden" name="country" value="${eidasModel.citizen}"/>
    <input type="hidden" name="sendmethods" value="POST"/>
    <input type="hidden" name="RelayState" value="${_csrf.token}" />
</form:form>
</html>
