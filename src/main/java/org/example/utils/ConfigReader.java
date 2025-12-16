package org.example.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static ConfigReader instance;

    private static final ThreadLocal<Properties> properties = new ThreadLocal<>();

    private ConfigReader() {
        loadProperties();
    }

    public static ConfigReader getInstance() {
        if (instance == null) {
            synchronized (ConfigReader.class) {
                if (instance == null) {
                    instance = new ConfigReader();
                }
            }
        }
        return instance;
    }

    private void loadProperties() {
        Properties props = new Properties();
        String env = System.getProperty("env", "qa");
        String configPath = "src/test/resources/config/config-" + env + ".properties";

        try (FileInputStream fis = new FileInputStream(configPath)) {
            props.load(fis);
            properties.set(props);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file: " + configPath, e);
        }
    }

    public String getBaseUrl() {
        if (properties.get() == null) {
            loadProperties();
        }
        return properties.get().getProperty("base.url");
    }

    public String getAuthToken() {
        if (properties.get() == null) {
            loadProperties();
        }
        return properties.get().getProperty("auth.token");
    }

    public int getTimeout() {
        if (properties.get() == null) {
            loadProperties();
        }
        return Integer.parseInt(properties.get().getProperty("timeout", "30000"));
    }

    public String getProperty(String key) {
        if (properties.get() == null) {
            loadProperties();
        }
        return properties.get().getProperty(key);
    }

    public void removeThreadLocalProperties() {
        properties.remove();
    }
}