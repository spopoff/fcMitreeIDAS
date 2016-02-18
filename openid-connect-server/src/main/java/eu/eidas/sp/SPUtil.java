package eu.eidas.sp;

import eu.eidas.auth.engine.EIDASSAMLEngine;
import eu.eidas.auth.engine.SAMLEngineUtils;
import eu.eidas.engine.exceptions.EIDASSAMLEngineException;
import eu.eidas.samlengineconfig.CertificateConfigurationManager;

import eu.eidas.sp.metadata.SPMetadataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Properties;

public class SPUtil {
	static final Logger logger = LoggerFactory.getLogger(SPUtil.class.getName());
	private static final String SAML_ENGINE_LOCATION_VAR="SP_CONF_LOCATION";

	private static Properties loadConfigs(String path) throws IOException
	{
		Properties properties = new Properties();
		properties.load(SPUtil.class.getClassLoader().getResourceAsStream(path));
		return properties;
	}

	public static Properties loadSPConfigs() throws ApplicationSpecificServiceException {
		try {
			return SPUtil.loadConfigs(Constants.SP_PROPERTIES);
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new ApplicationSpecificServiceException("Could not load configuration file", e.getMessage());
		}
	}

	/**
	 * @return true when the metadata support should be active
	 */
	public static boolean isMetadataEnabled(){
		return SPUtil.loadSPConfigs().getProperty(Constants.SP_METADATA_ACTIVATE)==null
				|| Boolean.parseBoolean(SPUtil.loadSPConfigs().getProperty(Constants.SP_METADATA_ACTIVATE));
	}
    static CertificateConfigurationManager spSamlEngineConfig=null;
	public static EIDASSAMLEngine createSAMLEngine(String samlEngineName) throws EIDASSAMLEngineException {
		if(spSamlEngineConfig==null && System.getenv(SAML_ENGINE_LOCATION_VAR)!=null){
			spSamlEngineConfig = ApplicationContextProvider.getApplicationContext().getBean(CertificateConfigurationManager.class);
			spSamlEngineConfig.setLocation(getLocation(System.getenv(SAML_ENGINE_LOCATION_VAR)));
		}
		EIDASSAMLEngine engine=null;
        if(spSamlEngineConfig != null && spSamlEngineConfig.isActive() && spSamlEngineConfig.getConfiguration() != null && !spSamlEngineConfig.getConfiguration().isEmpty()){
			engine = EIDASSAMLEngine.createSAMLEngine(samlEngineName,spSamlEngineConfig);
        }
        else {
            engine = EIDASSAMLEngine.createSAMLEngine(samlEngineName);
        }
		if(isMetadataEnabled()) {
			engine.setMetadataProcessor(new SPMetadataProvider());
		}
		return engine;
	}

	private static final String[] PATH_PREFIXES={"file://", "file:/","file:" };
	private static String getLocation(String location){
		if (location!=null){
			for(String prefix:PATH_PREFIXES){
				if(location.startsWith(prefix)){
					return location.substring(prefix.length());
				}
			}
		}
		return location;
	}

	public static String extractAssertionAsString(String samlMsg){
		return SAMLEngineUtils.extractAssertionAsString(samlMsg);
	}

}
