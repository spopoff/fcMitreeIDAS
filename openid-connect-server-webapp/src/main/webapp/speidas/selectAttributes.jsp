<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html lang="en">


<head>
	<jsp:include page="../speidas/htmlHead.jsp"/>
	<link href="../speidas/css/dd.css" rel="stylesheet" type="text/css" />
	<title><s:property value="%{getText('tituloId')}"/></title>
</head>

<body>
<!--START HEADER-->
<header class="header">
	<div class="container">
		<h1><s:property value="%{getText('tituloCabeceraId')}"/></h1>
	</div>
</header>
<!--END HEADER-->
<div class="container">
	<div class="row">
		<!--START NAV TAB-->
		<ul class="nav nav-tabs" role="tablist">
			<li role="presentation" class="active"><a href="#tab-02" aria-controls="tab-02" role="tab" data-toggle="tab">
				eIdas <span class="sub-title"><s:property value="%{getText('tab2Id')}"/></span></a>
			</li>
			<%--<li role="presentation"><a href="#tab-04" aria-controls="tab-04" role="tab" data-toggle="tab">error <span class="sub-title">page</span></a></li>--%>
		</ul>
		<!--END NAV TAB-->
		<!--START TAB-->
		<div class="tab-content">
			<!--START TAB-02-->
			<!-- ******************************************************************************************************************************** -->
			<!-- ************************************************* TABBED PANEL 2 EIDAS attributes*********************************************** -->
			<!-- ******************************************************************************************************************************** -->
			<div role="tabpanel" class="tab-pane fade in active" id="tab-02">
				<div class="col-md-12">
					<h2><s:property value="%{providerName}"/>
						<%--<span class="sub-title"><s:property value="%{getText('eIDASMode')}"/></span>--%>
						<span class="sub-title">(submits to an <span class="lowercase">e</span>IDAS Authentication Service)</span>
					</h2>
				</div>
				<jsp:include page="leftColumn.jsp"/>
				<div class="col-md-6">
					<s:form action="deposeIndexPage" id="formTab2">
						<h3 class="m-top-0">Detail messages</h3>
                                                <div class="form-group" id="nodeUrlDiv">
                                                    <s:textfield name="nodeUrl" id="caNodeUrl" cssClass="form-control" readonly="true"/>
                                                </div>
						<div class="form-group" id="citizenCountryDivEidas">
							<label for="citizenSelect"><s:property value="%{getText('citizenCountryId')}"/></label>
							<select name="citizenEidas" id="citizeneidas" class="form-control">
								<option data-description="Choose an option"></option>
								<s:iterator value="countries">
									<option value="<s:property value="name" />"
											data-image="../speidas/img/flags/<s:property value="name"/>.gif"><s:property
											value="name" /></option>
								</s:iterator>
							</select>
						</div>
						<div class="form-group" id="spReturnIdDiv">
							<label for="spReturnUrl" ><s:property value="%{getText('spReturnUrlId')}"/></label>
                                                        <s:textfield name="returnUrl" id="spReturnUrlEidas" cssClass="form-control" readonly="true"/>
						</div>
                                                <input type="hidden" id="spType" name="spType" value="public">
						<button id="submit_tab2" type="button" class="btn btn-default btn-lg btn-block">Submit</button>
						<s:fielderror />
                                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                                <input type="hidden" name="_csrf_header" content="${_csrf.headerName}"/>
					</s:form>
				</div>
			</div>
		</div>
	</div>
</div> <% /*end container*/ %>
<jsp:include page="../speidas/footer.jsp"/>
</body>