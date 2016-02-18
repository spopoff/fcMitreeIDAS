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

import java.util.HashSet;
import java.util.Set;
import org.opensaml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 *
 * @author mrwc1264
 */
public class SAML2AuthenticationProvider implements AuthenticationProvider{
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationProvider.class);
    private static final GrantedAuthority ROLE_ANONYMOUS = new SimpleGrantedAuthority("ROLE_ANONYMOUS");
    private static final GrantedAuthority ROLE_CLIENT = new SimpleGrantedAuthority("ROLE_CLIENT");
    private static final GrantedAuthority ROLE_ADMIN = new SimpleGrantedAuthority("ROLE_ADMIN");
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        LOG.debug("Arrive dans authenticate");
        Assertion asssert;
        asssert = (Assertion) authentication;
        String user = "toto";
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(ROLE_ANONYMOUS);
        authorities.add(ROLE_CLIENT);
        authorities.add(ROLE_ADMIN);
        return new SAML2AssertionAuthenticationToken(user, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (Assertion.class.isAssignableFrom(authentication));
    }
    
}
