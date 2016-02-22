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
import eu.eidas.auth.commons.EIDASAuthnResponse;
import eu.eidas.auth.commons.IPersonalAttributeList;
import eu.eidas.auth.commons.PersonalAttribute;
import eu.eidas.auth.engine.EIDASSAMLEngine;
import eu.eidas.engine.exceptions.EIDASSAMLEngineException;
import eu.eidas.sp.ApplicationContextProvider;
import eu.eidas.sp.Constants;
import eu.eidas.sp.SPUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.service.UserInfoService;
import org.mitre.openid.connect.service.impl.DefaultUserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

/**
 *
 * @author mrwc1264
 */
public class SAML3AssertionTokenEndpointFilter extends AbstractAuthenticationProcessingFilter{
    private static final Logger LOG = LoggerFactory.getLogger(SAML3AssertionTokenEndpointFilter.class);
    private final String SAML_VALIDATION_ERROR = "SAML_VALIDATION_ERROR";
    private static final GrantedAuthority ROLE_ANONYMOUS = new SimpleGrantedAuthority("ROLE_ANONYMOUS");
    private static final GrantedAuthority ROLE_ADMIN = new SimpleGrantedAuthority("ROLE_ADMIN");
    private static final GrantedAuthority ROLE_CLIENT = new SimpleGrantedAuthority("ROLE_CLIENT");
    private static final GrantedAuthority ROLE_USER = new SimpleGrantedAuthority("ROLE_USER");
    private final String RELAYSTATE = "RelayState";
    @Resource
    UserInfoService userInfServ;
    RelayStateRepositoryService relayStateRepository;
    
    public SAML3AssertionTokenEndpointFilter(){
        super("/respeidas");
    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        LOG.debug("Arrive dans attemptAuthentication");
        Authentication ret = null;
        String SAMLResponse = request.getParameter("SAMLResponse");
        String relayState = request.getParameter(RELAYSTATE);
        if(relayState==null){
            logger.debug("Pas de relayState null");
        }else if(relayState.isEmpty()){
            logger.debug("Pas de relayState vide");
        }else{
            if(relayStateRepository==null){
                relayStateRepository = ApplicationContextProvider.getApplicationContext().getBean(RelayStateRepositoryService.class);            
            }
            
            if(relayStateRepository.existRelayState(relayState)){
                logger.debug("retour avec relayState="+relayState);
            }else{
                logger.error("retour avec mauvais relayState="+relayState);
                throw new BadCredentialsException("bad csrf relayState");
            }
        }
        EIDASAuthnResponse authnResponse = null;
        IPersonalAttributeList personalAttributeList = null;
        logger.debug("Arrive dans filtre SAML attemptAuthentication");
        //spUrl = configs.getProperty(Constants.SP_URL);

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

        Set<GrantedAuthority> authorities = new HashSet<>();
        String userId = null;
        if(authnResponse.isFail()){
            throw new IOException("Saml Response is fail"+authnResponse.getMessage());			
        }else{	
            LOG.info("token saml valide cherche userId");
            personalAttributeList = authnResponse.getPersonalAttributeList();			

            for(PersonalAttribute pa:personalAttributeList){
                if(pa.getName().equalsIgnoreCase("personidentifier")){
                    userId = pa.getValue().get(0);
                    break;
                }
            }
        }
        if(userId==null){
            throw new IOException("Pas trouve personidentifier dans attributs SAML");
        }
        //ajoute les attributs de l'utilisateur
        for(String nom : lesNoms()){
            for(PersonalAttribute pa:personalAttributeList){
                if(pa.getName().equalsIgnoreCase(nom)){
                    authorities.add(new SimpleGrantedAuthority(pa.getValue().get(0)));
                    break;
                }
            }
        }
        //attention c'est un raccourci normalement il faut passer par authentProvider !!!
        authorities.add(ROLE_CLIENT);
        authorities.add(ROLE_USER);
        authorities.add(ROLE_ADMIN);
        authorities.add(ROLE_ANONYMOUS);
        SAML2AssertionAuthenticationToken authTok = new SAML2AssertionAuthenticationToken(userId,authorities);
        authTok.setDetails(personalAttributeList);
        UserInfo userInf = new SamlUserInfo(personalAttributeList);
        if(userInfServ!=null){
            ((DefaultUserInfoService)userInfServ).addUserInfo(userInf);
        }else{
            LOG.error("marche pas injection GRRRRR !");
        }
        authTok.setAuthnResponse(authnResponse);
        ret = ((Authentication) authTok);
        return ret;
    }
    /**
     * Liste réduite des attributs demandés via eIDAS le minimum vitale
     * @return 
     */
    private List<String> lesNoms(){
        List<String> ret = new ArrayList<>();
        ret.add("CurrentGivenName");
        ret.add("CurrentFamilyName");
        ret.add("Gender");
        ret.add("DateOfBirth");
        return ret;
    }

    public RelayStateRepositoryService getRelayStateRepository() {
        return relayStateRepository;
    }

    public void setRelayStateRepository(RelayStateRepositoryService relayStateRepository) {
        this.relayStateRepository = relayStateRepository;
    }
    
}
