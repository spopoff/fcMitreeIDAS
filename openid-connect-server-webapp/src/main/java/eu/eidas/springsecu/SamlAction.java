package eu.eidas.springsecu;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import eu.eidas.auth.commons.IPersonalAttributeList;
import eu.eidas.auth.commons.EIDASUtil;
import eu.eidas.auth.commons.PersonalAttribute;
import eu.eidas.auth.commons.EIDASAuthnResponse;
import eu.eidas.auth.engine.EIDASSAMLEngine;
import eu.eidas.engine.exceptions.EIDASSAMLEngineException;
import eu.eidas.sp.ApplicationSpecificServiceException;
import eu.eidas.sp.Constants;
import eu.eidas.sp.IndexAction;
import eu.eidas.sp.SPUtil;
import java.util.Base64;
import org.apache.commons.lang.StringUtils;
/**
 * This Action recives a SAML Response, shows it to the user and then validates it getting the attributes values
 * @author iinigo
 *
 */
public class SamlAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {
	
    private static final long serialVersionUID = 3660074009157921579L;
    private static final String SAML_VALIDATION_ERROR="Could not validate token for Saml Response";

    static final Logger logger = LoggerFactory.getLogger(IndexAction.class.getName());

    private String SAMLResponse;
    private String samlResponseXML;
    private String samlUnencryptedResponseXML;

    private List<PersonalAttribute> attrList;

    private HttpServletRequest request;
    private Properties configs;
    private String providerName;
    private String actionUrl;

    public String getActionUrl() {
        return actionUrl;
    }	//private static String spUrl;	

	/**
	 * Translates the SAMLResponse to XML format in order to be shown in the JSP
	 * @return
	 */
        @Override
	public String execute(){

            logger.debug("Arrive dans SAML execute");
            configs = SPUtil.loadSPConfigs();
            providerName = configs.getProperty(Constants.PROVIDER_NAME);
            byte[] decSamlToken = EIDASUtil.decodeSAMLToken(SAMLResponse);		
            samlResponseXML = new String(decSamlToken);
            try {
                EIDASSAMLEngine engine = SPUtil.createSAMLEngine(Constants.SP_CONF);
                //validate SAML Token
                engine.validateEIDASAuthnResponse(decSamlToken, request.getRemoteHost(), 0);

                boolean encryptedResponse=engine.isEncryptedSamlResponse(decSamlToken);
                if(encryptedResponse) {
                    final byte[] eidasTokenSAML = engine.checkAndResignEIDASTokenSAML(decSamlToken);
                    samlUnencryptedResponseXML = SPUtil.extractAssertionAsString(new String(eidasTokenSAML));
                }

            }catch(EIDASSAMLEngineException e){
                logger.error(e.getMessage());
                if(StringUtils.isEmpty(e.getErrorDetail())) {
                    throw new ApplicationSpecificServiceException(SAML_VALIDATION_ERROR, e.getErrorMessage());
                }else{
                    throw new ApplicationSpecificServiceException(SAML_VALIDATION_ERROR, e.getErrorDetail());
                }
            }
            actionUrl = "/saml?grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Asaml2-bearer&assertion=";
            actionUrl +=samlUnencryptedResponseXML;
            return Action.SUCCESS;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setServletResponse(HttpServletResponse response) {
	}
	
	public void setAttrList(List<PersonalAttribute> attrList) {
		this.attrList = attrList;
	}

	public List<PersonalAttribute> getAttrList() {
		return attrList;
	}	

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}	
	
//	public String getSpUrl() {
//		return spUrl;
//	}

//	public void setSpUrl(String spUrl) {
//		ReturnAction.spUrl = spUrl;
//	}

	public String getSAMLResponse() {
		return SAMLResponse;
	}

	public void setSAMLResponse(String samlResponse) {
		this.SAMLResponse = samlResponse;
	}

	public String getSamlResponseXML() {
		return samlResponseXML;
	}

	public void setSamlResponseXML(String samlResponseXML) {
		this.samlResponseXML = samlResponseXML;
	}

	public String getSamlUnencryptedResponseXML() {
		return samlUnencryptedResponseXML;
	}

	public void setSamlUnencryptedResponseXML(String samlUnencryptedResponseXML) {
		this.samlUnencryptedResponseXML = samlUnencryptedResponseXML;
	}
}