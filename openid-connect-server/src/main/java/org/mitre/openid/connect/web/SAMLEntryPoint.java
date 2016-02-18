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
package org.mitre.openid.connect.web;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.FilterInvocation;
import org.springframework.web.filter.GenericFilterBean;

/**
 * ne sert pratiquement a rien j'ai pas besoin d'un point d'initialisation
 * @author SPOPOFF
 */
public class SAMLEntryPoint extends GenericFilterBean implements AuthenticationEntryPoint{
    protected String filterProcessesUrl = FILTER_URL;
    public static final String FILTER_URL = "/eidas";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        FilterInvocation fi = new FilterInvocation(request, response, chain);

        if (processFilter(fi.getRequest())) {
            logger.debug("une requête EIDAS="+fi.getRequestUrl());
        }
        chain.doFilter(request, response);
    }
    /**
     * The filter will be used in case the URL of the request contains the DEFAULT_FILTER_URL.
     *
     * @param request request used to determine whether to enable this filter
     * @return true if this filter should be used
     */
    protected boolean processFilter(HttpServletRequest request) {
        return processFilter(filterProcessesUrl, request);
    }
    private boolean processFilter(String filterName, HttpServletRequest request) {
        return (request.getRequestURI().contains(filterName));
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
    }
    
}
