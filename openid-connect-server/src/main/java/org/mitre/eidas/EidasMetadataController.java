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
package org.mitre.eidas;

import org.mitre.eidas.EidasModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author SPOPOFF
 */
@Controller
public class EidasMetadataController {
    static final Logger LOGGER = LoggerFactory.getLogger(EidasMetadataController.class);
    
    @ResponseBody
    @RequestMapping(value = "/mdeidas", method = RequestMethod.GET, produces="application/samlmetadata+xml")
    public String sendMD() {
        EidasModel md = new EidasModel();
        md.metadataResponse();
        return md.getMetadata();
    }
}
