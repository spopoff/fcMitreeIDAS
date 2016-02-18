<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html lang="en">

<body onload="document.redirectForm.submit();">
<form:form id="redirectForm" name="redirectForm" method="post" action="${nodeUrl}">
    <input type="hidden" id="SAMLRequest" name="SAMLRequest" value="${SAMLRequest}"/>
    <input type="hidden" name="country" value="${citizen}"/>
    <input type="hidden" name="sendmethods" value="POST"/>
</form:form>
</html>
