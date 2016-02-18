<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html dir="ltr">
<head>
    <meta http-equiv='pragma' content='no-cache'/>
    <meta http-equiv='cache-control' content='no-cache, no-store, must-revalidate'/>
	<meta http-equiv="Expires" content="-1"/>
	<title><s:property value="%{getText('tituloId')}" /></title>
<link href="css/estilos.css" rel="stylesheet" type="text/css" />
</head>
<body>

<div id="contenedor">
<div id="cabecera">
<div class="logo"></div>
<div class="tituloCabecera"><s:property
	value="%{getText('tituloCabeceraId')}" /></div>
</div>
<div id="borde">
<div id="principal">
<div id="margen">


<h1><s:property value="%{providerName}" /></h1>
<br />
<%

String providerName = (String) request.getAttribute("providerName");
String spId = (String) request.getAttribute("spId");
String spUrl= (String) request.getAttribute("spUrl");
String spQaaLevel = (String) request.getAttribute("spQaaLevel");
String nodeCountryForm = (String) request.getAttribute("nodeCountryForm");
String attrList = (String) request.getAttribute("attrList");
String spSector = (String) request.getAttribute("spSector");
String spInstitution = (String) request.getAttribute("spInstitution");
String spApplication = (String) request.getAttribute("spApplication");
String spCountry = (String) request.getAttribute("spCountry");
String spMetadataUrl = (String) request.getAttribute("spmetadataurl");
String enc="UTF-8";

 %>
<h2><s:property value="%{getText('selectCountryId')}" />:</h2>
<br />
<iframe
	src="<%=nodeCountryForm%>?spId=<%=spId%>&providerName=<%=providerName%>&spUrl=<%=spUrl%>&spQaaLevel=<%=spQaaLevel%>&attrList=<%=java.net.URLEncoder.encode(attrList, enc)%>&spSector=<%=spSector%>&spInstitution=<%=spInstitution%>&spApplication=<%=spApplication%>&spCountry=<%=spCountry%>&spmetadataurl=<%=spMetadataUrl%>"
	width="100%" height="250px" style="border: 0px;"></iframe></div>
</div>
</div>
</div>


</body>
</html>
