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
package eu.eidas.sp;

import org.mitre.eidas.EidasModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Ne sert à rien fait un filtre
 * @author SPOPOFF
 */
@Controller
public class EidasResponseController {
    static final Logger logger = LoggerFactory.getLogger(EidasResponseController.class);
    
    @RequestMapping(value = "/respeidas", method = RequestMethod.POST)
    public String responseSaml(@ModelAttribute EidasModel eidasModel, Model model){
        logger.info("saml response return");
        eidasModel.responseSAML();
        model.addAttribute("eidasModel", eidasModel);
        logger.info("saml response aquired");
        return null;
    }
}
