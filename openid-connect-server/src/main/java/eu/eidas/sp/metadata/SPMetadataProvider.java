/*
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence. You may
 * obtain a copy of the Licence at:
 *
 * http://www.osor.eu/eupl/european-union-public-licence-eupl-v.1.1
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * Licence for the specific language governing permissions and limitations under
 * the Licence.
 */
package eu.eidas.sp.metadata;

import eu.eidas.auth.engine.EIDASSAMLEngine;
import eu.eidas.auth.engine.metadata.SimpleMetadataProcessor;
import eu.eidas.engine.exceptions.SAMLEngineException;
import eu.eidas.sp.SPUtil;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.SPSSODescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyStore;

public class SPMetadataProvider extends SimpleMetadataProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(SPMetadataProvider.class.getName());

    public EntityDescriptor getEntityDescriptor(String url){
        EntityDescriptor entityDescriptor=null;
        if(SPUtil.isMetadataEnabled()) {
            entityDescriptor=getEntityDescriptorHelper(url);
        }
        if(LOG.isDebugEnabled()){
            LOG.debug("got entityDescriptor {}", entityDescriptor);
        }
        return entityDescriptor;
    }

    @Override
    public SPSSODescriptor getSPSSODescriptor(String url) throws SAMLEngineException {
        return null;
    }

    @Override
    public IDPSSODescriptor getIDPSSODescriptor(String url) throws SAMLEngineException {
        return getFirstRoleDescriptor(getEntityDescriptor(url), IDPSSODescriptor.class);
    }

    @Override
    public void checkValidMetadataSignature(String url, EIDASSAMLEngine engine) throws SAMLEngineException {
        //not implemented
        LOG.warn("MetadataProcessor in demo SP does not actually check the signature of metadata");
    }
    @Override
    public void checkValidMetadataSignature(String url, KeyStore store) throws SAMLEngineException {
        //not implemented
    }
}

