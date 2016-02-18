/*
 * Copyright 2016 SPOPOFF.
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
package eu.eidas.sp.metadata;

import eu.eidas.auth.engine.EIDASSAMLEngine;
import eu.eidas.auth.engine.metadata.MetadataConfigParams;
import eu.eidas.auth.engine.metadata.MetadataGenerator;
import eu.eidas.engine.exceptions.EIDASSAMLEngineException;
import eu.eidas.engine.exceptions.SAMLEngineException;
import eu.eidas.sp.Constants;
import eu.eidas.sp.SPUtil;
import java.io.IOException;
import java.util.Properties;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.web.FilterInvocation;
import org.springframework.web.filter.GenericFilterBean;

/**
 *
 * @author SPOPOFF
 */
public class MetadataDisplayFilter extends GenericFilterBean{

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        FilterInvocation fi = new FilterInvocation(request, response, chain);
        processMetadataDisplay(fi.getHttpRequest(), fi.getHttpResponse());
    }
    protected void processMetadataDisplay(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String metadata="invalid metadata";
        if(SPUtil.isMetadataEnabled()) {
            try {
                Properties configs = SPUtil.loadSPConfigs();
                EIDASSAMLEngine engine = SPUtil.createSAMLEngine(Constants.SP_CONF);
                MetadataGenerator generator = new MetadataGenerator();
                MetadataConfigParams mcp=new MetadataConfigParams();
                generator.setConfigParams(mcp);
                generator.initialize(engine);
                mcp.setEntityID(configs.getProperty(Constants.SP_METADATA_URL));
                generator.addSPRole();
                String returnUrl = SPUtil.loadSPConfigs().getProperty(Constants.SP_RETURN);
                mcp.setAssertionConsumerUrl(returnUrl);
                metadata = generator.generateMetadata();
            }catch (SAMLEngineException see) {
                logger.error("error generating metadata {}", see);
                throw new IOException(see);
            }catch(EIDASSAMLEngineException see){
                logger.error("error generating metadata {}", see);
                throw new IOException(see);
            }
        }
        response.setContentType("text/html");
        response.getWriter().print(metadata);
        
    }
    
}
