/*
 * Copyright 2016 mrwc1264.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mitre.openid.connect.assertion;

import eu.eidas.auth.commons.EIDASUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.impl.AssertionUnmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @deprecated
 * @author mrwc1264
 */
public class SAML2AssertionTokenEndpointFilter extends AbstractAuthenticationProcessingFilter{
    private static final Logger LOG = LoggerFactory.getLogger(SAML2AssertionTokenEndpointFilter.class);
    public SAML2AssertionTokenEndpointFilter(RequestMatcher additionalMatcher){
        super(additionalMatcher);
    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        LOG.debug("Arrive dans attemptAuthentication");
        Authentication ret = null;
        Object asssert = request.getParameter("assertion");
        byte[] ass = EIDASUtil.decodeSAMLToken((String)asssert);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException p) {
            throw new IOException(p);
        }
        Document document = null;
        try {
            document = docBuilder.parse(new ByteArrayInputStream(ass));
        } catch (SAXException s) {
            throw new IOException(s);
        } catch (IOException i) {
            throw new IOException(i);
        }
        Element element = document.getDocumentElement();
        AssertionUnmarshaller assUn = new AssertionUnmarshaller();
        try {
            Assertion asss = (Assertion) assUn.unmarshall(XMLHelper.constructElement(document, Assertion.DEFAULT_ELEMENT_NAME));
        } catch (UnmarshallingException u) {
            throw new IOException(u);
        }
        ret = this.getAuthenticationManager().authenticate((Authentication) asssert);
        return ret;
    }
    
}
