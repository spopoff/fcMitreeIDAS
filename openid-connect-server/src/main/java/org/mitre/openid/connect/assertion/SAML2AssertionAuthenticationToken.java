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

import eu.eidas.auth.commons.EIDASAuthnResponse;
import eu.eidas.auth.commons.IPersonalAttributeList;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author mrwc1264
 */
public class SAML2AssertionAuthenticationToken extends AbstractAuthenticationToken{
    private final String user;
    private IPersonalAttributeList personalAttributeList = null;
    private EIDASAuthnResponse authnResponse;
    private static final Logger LOG = LoggerFactory.getLogger(SAML2AssertionAuthenticationToken.class);
    
    public SAML2AssertionAuthenticationToken(String user, Collection<? extends GrantedAuthority> authorities){
       super(authorities);
       this.user = user;
       setAuthenticated(true);
       
    }
    @Override
    public Object getCredentials() {
        return authnResponse;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }

    public void setAuthnResponse(EIDASAuthnResponse authnResponse) {
        this.authnResponse = authnResponse;
    }
    @Override
    public Object getDetails() {
        return personalAttributeList;
    }

    @Override
    public void setDetails(Object details) {
        super.setDetails(details);
        if(details instanceof IPersonalAttributeList){
            personalAttributeList = (IPersonalAttributeList) details;
        }else{
            LOG.error("les attributs personnels de correspondent pas au type IPersonalAttributeList");
        }
    }
    
}
