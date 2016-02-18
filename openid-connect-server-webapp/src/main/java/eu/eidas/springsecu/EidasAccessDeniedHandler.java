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
package eu.eidas.springsecu;

import eu.eidas.auth.commons.EIDASAuthnResponse;
import eu.eidas.auth.commons.EIDASUtil;
import eu.eidas.auth.commons.IPersonalAttributeList;
import eu.eidas.auth.engine.EIDASSAMLEngine;
import eu.eidas.engine.exceptions.EIDASSAMLEngineException;
import eu.eidas.sp.ApplicationSpecificServiceException;
import eu.eidas.sp.Constants;
import eu.eidas.sp.SPUtil;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 *
 * @author mrwc1264
 */
public class EidasAccessDeniedHandler implements AccessDeniedHandler{
    static final Logger logger = LoggerFactory.getLogger(EidasAccessDeniedHandler.class);
    private String SAMLResponse;
    private static final String SAML_VALIDATION_ERROR="Could not validate token for Saml Response";
    
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        logger.debug("Arrive dans handle error");
        EIDASAuthnResponse authnResponse = null;
        SAMLResponse = request.getParameter("SAMLresponse");
        if(SAMLResponse==null){
            throw new IOException("Saml Response is null");
        }
        if(SAMLResponse.isEmpty()){
            throw new IOException("Saml Response is empty");
        }
        //Decodes SAML Response
        byte[] decSamlToken = EIDASUtil.decodeSAMLToken(SAMLResponse);

        //Get SAMLEngine instance

        try {
            EIDASSAMLEngine engine = SPUtil.createSAMLEngine(Constants.SP_CONF);
            //validate SAML Token
            authnResponse = engine.validateEIDASAuthnResponse(decSamlToken, request.getRemoteHost(), 0);

        }catch(EIDASSAMLEngineException e){
            logger.error(e.getMessage());
            if(StringUtils.isEmpty(e.getErrorDetail())) {
                throw new IOException(SAML_VALIDATION_ERROR, e);
            }else{
                throw new IOException(SAML_VALIDATION_ERROR, e);
            }
        }			

        if(authnResponse.isFail()){
            throw new IOException("Saml Response is fail"+authnResponse.getMessage());			
        }else{
            request.setAttribute("assertions", authnResponse.getAsserts());
            RequestDispatcher dispatch;
            dispatch = request.getRequestDispatcher("/saml");
            logger.debug("Avant forward vers saml");
            dispatch.forward(request, response);
        }
        
    }
    
}
