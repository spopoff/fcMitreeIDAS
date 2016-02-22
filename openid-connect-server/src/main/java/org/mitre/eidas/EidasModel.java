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

import eu.eidas.auth.commons.EIDASAuthnRequest;
import eu.eidas.auth.commons.EIDASAuthnResponse;
import eu.eidas.auth.commons.EIDASStatusCode;
import eu.eidas.auth.commons.EIDASUtil;
import eu.eidas.auth.commons.EidasLoaCompareType;
import eu.eidas.auth.commons.EidasLoaLevels;
import eu.eidas.auth.commons.IPersonalAttributeList;
import eu.eidas.auth.commons.PersonalAttribute;
import eu.eidas.auth.commons.PersonalAttributeList;
import eu.eidas.auth.engine.EIDASSAMLEngine;
import eu.eidas.auth.engine.metadata.MetadataConfigParams;
import eu.eidas.auth.engine.metadata.MetadataGenerator;
import eu.eidas.engine.exceptions.EIDASSAMLEngineException;
import eu.eidas.engine.exceptions.SAMLEngineException;
import eu.eidas.sp.ApplicationContextProvider;
import eu.eidas.sp.ApplicationSpecificServiceException;
import eu.eidas.sp.Constants;
import eu.eidas.sp.SPUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.mitre.openid.connect.assertion.RelayStateRepositoryService;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.impl.AssertionMarshaller;
import org.opensaml.xml.io.MarshallingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author SPOPOFF
 */
public class EidasModel {
    static final Logger LOGGER = LoggerFactory.getLogger(EidasModel.class);
    public final String ATTRIBUTE_SIGNED_DOC = "signedDoc";
    private final String SPTYPE_PARAM="spType";

    private HttpServletRequest request;
    private String SAMLRequest;
    private String samlRequestXML;	

    private Properties configs; 
    private List<Country> countries;
    private List<PersonalAttribute> storkAttributeList;
    private List<PersonalAttribute> eidasAttributeList;

    private String spId;
    private String providerName;
    private String spSector;
    //private static String spInstitution;
    private String spApplication;
    private String spCountry = "CC";

    /*Requested parameters*/
    private String nodeUrl; 
    private String nodeUrl2;	
    private String qaa;
    private String citizen;
    private String returnUrl;
    private String eidasloa;
    private String eidasloaCompareType;
    private String eidasNameIdentifier;
    private String eidasSPType;
    private boolean eidasNodeOnly=true;
    private String metadata="invalid metadata";
    private String SAMLResponse;
    private String samlResponseXML;
    private String samlUnencryptedResponseXML;
    private String relayState;

    public EidasModel(){
        loadGlobalConfig();
        populate();
    }
    private void loadGlobalConfig(){
        configs = SPUtil.loadSPConfigs();
        spId = configs.getProperty(Constants.PROVIDER_NAME);
        providerName = configs.getProperty(Constants.PROVIDER_NAME);
        spSector = configs.getProperty(Constants.SP_SECTOR);
        //spInstitution = configs.getProperty(Constants.PROVIDER_NAME);
        spApplication = configs.getProperty(Constants.SP_APLICATION);
        spCountry = configs.getProperty(Constants.SP_COUNTRY);
        countries = new ArrayList<Country> ();
        eidasNodeOnly=!(Boolean.FALSE.toString().equalsIgnoreCase(configs.getProperty(Constants.SP_EIDAS_ONLY)));

    }
    /**
 	 * Fill the data in the JSP that is shown to the user in order to fill the requested data to generate a saml request
     * @return ACTION_REDIRECT
     */
    private void populate() {		

        nodeUrl = configs.getProperty("nodeUrl");
        LOGGER.debug("nodeUrl="+nodeUrl);
        returnUrl = configs.getProperty(Constants.SP_RETURN);
        qaa = configs.getProperty(Constants.SP_QAALEVEL);		

        int numCountries = Integer.parseInt(configs.getProperty(Constants.COUNTRY_NUMBER));
        for(int i=1;i<=numCountries;i++){
            Country country = new Country(i,configs.getProperty("country" + Integer.toString(i) + ".name"), configs.getProperty("country" + Integer.toString(i) + ".url"), configs.getProperty("country" + Integer.toString(i) + ".countrySelector"));
            countries.add(country);
            LOGGER.debug(country.toString());
        }

    }
    public void depose(){
        LOGGER.debug("Arrive dans depose pour citizen="+citizen);
        //citizen = request.getParameter("citizenEidas");
        if(citizen.isEmpty()){
            LOGGER.debug("Marche pas liste pays force CB");
            citizen = "CB";
        }
        configs = SPUtil.loadSPConfigs();
        eidasNameIdentifier = configs.getProperty("eidasNameIdentifier");
        qaa = configs.getProperty("sp.qaalevel");
        eidasSPType = configs.getProperty("sp.type");
        LOGGER.debug("eidasNameIdentifier="+eidasNameIdentifier+" qaa="+qaa+" eidasSPType="+eidasSPType);
        IPersonalAttributeList pAttList = new PersonalAttributeList();
        pAttList = extraitAttributs(lesNoms());
        boolean eIdasRequest=true;
        //Iterate through the request parameters looking for SAML Engine attributes
        byte[] token = null;

        EIDASAuthnRequest authnRequest = new EIDASAuthnRequest();

        authnRequest.setDestination(nodeUrl);
        authnRequest.setProviderName(providerName);
        if(qaa!=null) {
                authnRequest.setQaa(Integer.parseInt(qaa));
        }else{
            LOGGER.error("Il faut une valeur à qaa!!");
        }
        authnRequest.setPersonalAttributeList(pAttList);
        eidasloa = configs.getProperty("sp.loalevel");
        eidasloaCompareType = configs.getProperty("sp.eidasloaCompareType");
        if(eIdasRequest) {
            if(EidasLoaLevels.getLevel(eidasloa)==null) {
                authnRequest.setEidasLoA(EidasLoaLevels.LOW.stringValue());
            }else {
                authnRequest.setEidasLoA(eidasloa);
            }
            authnRequest.setSPType(eidasSPType);
            authnRequest.setEidasLoACompareType(EidasLoaCompareType.getCompareType(eidasloaCompareType).stringValue());
            authnRequest.setEidasNameidFormat(eidasNameIdentifier);
            authnRequest.setBinding(EIDASAuthnRequest.BINDING_EMPTY);
        }else{
                authnRequest.setAssertionConsumerServiceURL(returnUrl);
        }
        String metadataUrl=configs.getProperty(Constants.SP_METADATA_URL);
        if(metadataUrl!=null && !metadataUrl.isEmpty() && SPUtil.isMetadataEnabled()) {
                authnRequest.setIssuer(metadataUrl);
        }

            //new parameters
        authnRequest.setSpSector(spSector);
//		authnRequest.setSpInstitution(spInstitution);
        authnRequest.setSpApplication(spApplication);
        authnRequest.setSpCountry(spCountry);

        //V-IDP parameters
        authnRequest.setCitizenCountryCode(citizen);
        authnRequest.setSPID(spId);
        authnRequest.setEidasNameidFormat(eidasNameIdentifier);

        try{
            EIDASSAMLEngine engine = SPUtil.createSAMLEngine(Constants.SP_CONF);
            engine.initRequestedAttributes(pAttList);
            authnRequest = engine.generateEIDASAuthnRequest(authnRequest);
        }catch(EIDASSAMLEngineException e){
            LOGGER.error(e.getMessage());
            throw new ApplicationSpecificServiceException("Could not generate token for Saml Request", e.getErrorMessage());
        }	

        token = authnRequest.getTokenSaml();

        SAMLRequest = EIDASUtil.encodeSAMLToken(token);
        samlRequestXML = new String(token);
        LOGGER.debug("samlRequestXML="+samlRequestXML);
    }
    public void metadataResponse(){
        if(SPUtil.isMetadataEnabled()) {
            try {
                configs = SPUtil.loadSPConfigs();
                EIDASSAMLEngine engine = SPUtil.createSAMLEngine(Constants.SP_CONF);
                MetadataGenerator generator = new MetadataGenerator();
                MetadataConfigParams mcp=new MetadataConfigParams();
                generator.setConfigParams(mcp);
                generator.initialize(engine);
                mcp.setEntityID(configs.getProperty(Constants.SP_METADATA_URL));
                generator.addSPRole();
                returnUrl = SPUtil.loadSPConfigs().getProperty(Constants.SP_RETURN);
                mcp.setAssertionConsumerUrl(returnUrl);
                metadata = generator.generateMetadata();
            }catch (SAMLEngineException see) {
                LOGGER.error("error generating metadata {}", see);
            }catch(EIDASSAMLEngineException see){
                LOGGER.error("error generating metadata {}", see);
            }
        }
        
    }
    public void responseSAML(){
        LOGGER.debug("Arrive dans SAML execute");
        configs = SPUtil.loadSPConfigs();
        providerName = configs.getProperty(Constants.PROVIDER_NAME);
        byte[] decSamlToken = EIDASUtil.decodeSAMLToken(SAMLResponse);		
        samlResponseXML = new String(decSamlToken);
        Assertion asssert = null;
        org.w3c.dom.Element element = null;
        try {
            EIDASSAMLEngine engine = SPUtil.createSAMLEngine(Constants.SP_CONF);
            //validate SAML Token
            EIDASAuthnResponse authResp = engine.validateEIDASAuthnResponse(decSamlToken, request.getRemoteHost(), 0);

            boolean encryptedResponse=engine.isEncryptedSamlResponse(decSamlToken);
            if(encryptedResponse) {
                final byte[] eidasTokenSAML = engine.checkAndResignEIDASTokenSAML(decSamlToken);
                samlUnencryptedResponseXML = SPUtil.extractAssertionAsString(new String(eidasTokenSAML));
            }else{
                asssert = authResp.getAsserts().get(0);
            }
            AssertionMarshaller marshaller = new AssertionMarshaller();
            element = marshaller.marshall(asssert);
        }catch(EIDASSAMLEngineException e){
            LOGGER.error(e.getMessage());
            if(StringUtils.isEmpty(e.getErrorDetail())) {
                LOGGER.error("Erreur validation "+e.getErrorMessage());
                return;
            }else{
                LOGGER.error("Erreur validation "+e.getErrorDetail());
                return;
            }
        }catch(MarshallingException e){
            LOGGER.error("erreur extration assertion "+e);
            return;
        }
        if(element==null){
            LOGGER.error("erreur element xml nulle");
            return;
        }
        if(asssert==null){
            LOGGER.error("erreur assertion nulle");
            return;
        }
        LOGGER.debug("assertion ID="+asssert.getID());
        
    }
    private List<String> lesNoms(){
        List<String> ret = new ArrayList<>();
        ret.add("PersonIdentifier");
        ret.add("CurrentGivenName");
        ret.add("CurrentFamilyName");
        ret.add("Gender");
        ret.add("DateOfBirth");
        return ret;
    }
    /**
     * change en une liste d'attribut
     * @param List<String> noms
     * @return IPersonalAttributeList
     * @throws Exception 
     */
    private IPersonalAttributeList extraitAttributs(List<String> noms){
        LOGGER.debug("json names nb="+noms.size());
        IPersonalAttributeList attrList = new PersonalAttributeList();
        for(String key : noms){
            LOGGER.debug("avant json key="+key);
            PersonalAttribute pa = new PersonalAttribute();
            pa.setName(key);
            pa.setIsRequired(false);
            pa.setStatus(EIDASStatusCode.STATUS_AVAILABLE.toString());
            attrList.add(pa);
        }
        return attrList;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

    public void setNodeUrl(String nodeUrl) {
        this.nodeUrl = nodeUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getNodeUrl() {
        return nodeUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public String getCitizen() {
        return citizen;
    }

    public void setCitizen(String citizen) {
        this.citizen = citizen;
    }

    public String getSAMLRequest() {
        return SAMLRequest;
    }

    public void setSAMLRequest(String SAMLRequest) {
        this.SAMLRequest = SAMLRequest;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getSAMLResponse() {
        return SAMLResponse;
    }

    public void setSAMLResponse(String SAMLResponse) {
        this.SAMLResponse = SAMLResponse;
    }

    public String getRelayState() {
        return relayState;
    }

    public void setRelayState(String relayState) {
        this.relayState = relayState;
    }
}
