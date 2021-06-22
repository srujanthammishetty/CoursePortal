package com.proximity.assignment.api;

import com.proximity.assignment.commons.Constants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @author sthammishetty on 18/06/21
 */
public class PropertiesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesService.class);
    private static Properties deploymentProperties;

    public static synchronized void loadPropertiesFromFileSystem() {
        if (deploymentProperties == null) {
            deploymentProperties = new Properties();
            InputStreamReader inputStreamReader = null;
            try {
                inputStreamReader = new InputStreamReader(new ClassPathResource(Constants.DEPLOYMENT_PROPERTIES).getInputStream());
                deploymentProperties.load(inputStreamReader);
            } catch (Exception e) {
                String msg = "Error loading application deployment properties";
                LOGGER.error(msg, e);
                throw new RuntimeException(msg, e);
            } finally {
                if (inputStreamReader != null) {
                    try {
                        inputStreamReader.close();
                    } catch (Exception e) {
                        LOGGER.error("Error closing file inputstream", e);
                    }
                }
            }
        }
    }

    public static String getValue(String key, String defaultValue) {
        return StringUtils.defaultIfBlank(deploymentProperties.getProperty(key).trim(), defaultValue);
    }

}
