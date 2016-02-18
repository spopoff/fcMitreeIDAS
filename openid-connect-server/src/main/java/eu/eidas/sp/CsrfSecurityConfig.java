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
package eu.eidas.sp;

import javax.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 *
 * @author mrwc1264
 */
@Configuration
@EnableWebMvcSecurity
public class CsrfSecurityConfig extends WebSecurityConfigurerAdapter{
    
  @Override
  protected void configure(HttpSecurity http) throws Exception {

    // Build the request matcher for CSFR protection
    RequestMatcher csrfRequestMatcher = new RequestMatcher() {

      // Disable CSFR protection on the following urls:
      private AntPathRequestMatcher[] requestMatchers = {
          new AntPathRequestMatcher("/respeidas", "POST"),
          new AntPathRequestMatcher("/mdeidas", "GET")
      };

      @Override
      public boolean matches(HttpServletRequest request) {
        // If the request match one url the CSFR protection will be disabled
        for (AntPathRequestMatcher rm : requestMatchers) {
          if (rm.matches(request)) { return false; }
        }
        return true;
      } // method matches

    }; // new RequestMatcher

    // Set security configurations
    http
      // Disable the csrf protection on some request matches
      .csrf().disable();
        //.requireCsrfProtectionMatcher(csrfRequestMatcher).and();
  } // method configure    
}
