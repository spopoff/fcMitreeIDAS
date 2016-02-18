package eu.eidas.sp;

import org.mitre.eidas.Country;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.eidas.auth.commons.*;
import eu.eidas.auth.engine.EIDASSAMLEngine;
import eu.eidas.auth.engine.core.validator.eidas.EIDASAttributes;
import eu.eidas.engine.exceptions.EIDASSAMLEngineException;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 
 * @author iinigo
 * This Action Generates a SAML Request with the data given by the user, then sends it to the selected node
 *
 */

public class IndexAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

    private static final long serialVersionUID = 3660074009157921579L;
	
    static final Logger LOGGER = LoggerFactory.getLogger(IndexAction.class.getName());
    public static final String ACTION_REDIRECT = "redirect";
    public static final String ACTION_POPULATE = "populate";
    public static final String ATTRIBUTE_SIGNED_DOC = "signedDoc";
    private static final String SPTYPE_PARAM="spType";

    private HttpServletRequest request;
    private String SAMLRequest;
    private String samlRequestXML;	

    private static Properties configs; 
    private static List<Country> countries;
    private static List<PersonalAttribute> storkAttributeList;
    private static List<PersonalAttribute> eidasAttributeList;

    private static String spId;
    private static String providerName;
    private static String spSector;
    //private static String spInstitution;
    private static String spApplication;
    private static String spCountry;

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
    Map<String, String> eidasAttribsInverseMap=inverseMap(EIDASAttributes.ATTRIBUTES_TO_SHORTNAMES);
    private static boolean eidasNodeOnly=true;


    private static void loadGlobalConfig(){
        configs = SPUtil.loadSPConfigs();
        spId = configs.getProperty(Constants.PROVIDER_NAME);
        providerName = configs.getProperty(Constants.PROVIDER_NAME);
        spSector = configs.getProperty(Constants.SP_SECTOR);
        //spInstitution = configs.getProperty(Constants.PROVIDER_NAME);
        spApplication = configs.getProperty(Constants.SP_APLICATION);
        spCountry = configs.getProperty(Constants.SP_COUNTRY);
        countries = new ArrayList<Country> ();
        storkAttributeList = new ArrayList<PersonalAttribute>();
        eidasAttributeList = new ArrayList<PersonalAttribute>();
        eidasNodeOnly=!(Boolean.FALSE.toString().equalsIgnoreCase(configs.getProperty(Constants.SP_EIDAS_ONLY)));

    }
    /**
 	 * Fill the data in the JSP that is shown to the user in order to fill the requested data to generate a saml request
     * @return ACTION_REDIRECT
     */
    public String populate() {		

        IndexAction.loadGlobalConfig();
        nodeUrl = configs.getProperty("nodeUrl");
        LOGGER.debug("nodeUrl="+nodeUrl);
        returnUrl = configs.getProperty(Constants.SP_RETURN);
        qaa = configs.getProperty(Constants.SP_QAALEVEL);		

        int numCountries = Integer.parseInt(configs.getProperty(Constants.COUNTRY_NUMBER));
        for(int i=1;i<=numCountries;i++){
            //Country country = new Country(i,configs.getProperty("country" + Integer.toString(i) + ".name"), configs.getProperty("country" + Integer.toString(i) + ".url"), configs.getProperty("country" + Integer.toString(i) + ".countrySelector"));
            //countries.add(country);
            //LOGGER.debug(country.toString());
        }

        return ACTION_POPULATE;
    }
	

    private Map<String, String> inverseMap(Map<String, String> theMap){
        Map<String, String> result=new HashMap<String, String>();
        for(Map.Entry<String, String> entry:theMap.entrySet()){
                result.put(entry.getValue(), entry.getKey());
        }
        return result;
    }
	
	/**
	 * Generates de Saml Request with the data given by the user
     * @return Action.SUCCESS
	 */
    public String depose(){
        LOGGER.debug("Arrive dans depose pour citizen="+citizen);
        citizen = request.getParameter("citizenEidas");
        if(citizen.isEmpty()){
            LOGGER.debug("Marche pas liste pays force CB");
            citizen = "CB";
        }
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
        authnRequest.setSpCountry(request.getParameter("connector_ms_input"));

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
        return Action.SUCCESS;
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
        LOG.debug("json names nb="+noms.size());
        IPersonalAttributeList attrList = new PersonalAttributeList();
        for(String key : noms){
            LOG.debug("avant json key="+key);
            PersonalAttribute pa = new PersonalAttribute();
            pa.setName(key);
            pa.setIsRequired(false);
            pa.setStatus(EIDASStatusCode.STATUS_AVAILABLE.toString());
            attrList.add(pa);
        }
        return attrList;
    }

    public List<PersonalAttribute> getStorkAttributeList() {
            return storkAttributeList;
    }
    public List<PersonalAttribute> getEidasAttributeList() {
            return eidasAttributeList;
    }

    public void setSAMLRequest(String samlToken) {
            this.SAMLRequest = samlToken;
    }

    public String getSAMLRequest() {
            return SAMLRequest;
    }	

    public String getQaa() {
            return qaa;
    }

    public void setQaa(String qaa) {
            this.qaa = qaa;
    }	

    public String getSpId() {
            return spId;
    }

    public String getProviderName() {
            return providerName;
    }

    public String getCitizen() {
            return citizen;
    }

    public void setCitizen(String citizen) {
            this.citizen = citizen;
    }

    public void setCitizenEidas(String citizen) {
            setCitizen(citizen);
    }

    public String getSamlRequestXML() {
            return samlRequestXML;
    }

    public void setSamlRequestXML(String samlRequestXML) {
            this.samlRequestXML = samlRequestXML;
    }

    public String getReturnUrl() {
            return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
            this.returnUrl = returnUrl;
    }	

    public String getNodeUrl() {
            return nodeUrl;
    }

    public void setNodeUrl(String nodeUrl) {
            this.nodeUrl = nodeUrl;
    }

    public List<Country> getCountries() {
            return countries;
    }


    @Override
    public void setServletRequest(HttpServletRequest request) {
            this.request = request;
    }

    @Override
    public void setServletResponse(HttpServletResponse response) {
    }

    public String getNodeUrl2() {
            return nodeUrl2;
    }

    public void setNodeUrl2(String nodeUrl2) {
            this.nodeUrl2 = nodeUrl2;
    }

    public String getEidasloa() {
        return eidasloa;
    }

    public void setEidasloa(String eidasloa) {
        this.eidasloa = eidasloa;
    }

    public String getEidasloaCompareType() {
            return eidasloaCompareType;
    }

    public void setEidasloaCompareType(String eidasloaCompareType) {
            this.eidasloaCompareType = eidasloaCompareType;
    }

    public String getEidasNameIdentifier() {
            return eidasNameIdentifier;
    }

    public void setEidasNameIdentifier(String eidasNameIdentifier) {
            this.eidasNameIdentifier = eidasNameIdentifier;
    }

    public String getEidasSPType() {
            return eidasSPType;
    }

    public void setEidasSPType(String eidasSPType) {
            this.eidasSPType = eidasSPType;
    }

    public boolean isEidasNodeOnly() {
            return eidasNodeOnly;
    }

    public void setEidasNodeOnly(boolean eidasNodeOnly) {
            IndexAction.setGlobalEidasNodeOnly(eidasNodeOnly);
    }
    public static void setGlobalEidasNodeOnly(boolean eidasNodeOnly) {
            IndexAction.eidasNodeOnly = eidasNodeOnly;
    }
}
