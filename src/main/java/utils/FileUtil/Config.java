package utils.FileUtil;

import java.util.HashMap;
import java.util.Map;

public class Config {

    // Enum to define keys for different singleton instances
    public enum InstanceType {
        CONFIG, CREDENTIAL
    }

    // Static map to hold singleton instances
    private static final Map<InstanceType, Config> instances = new HashMap<>();
    private Map<String, Object> envConfig;
    private Map<String, Object> allConfig;

    public final String configFilePath;

    public Config(String filePath) {
        configFilePath = filePath;
        allConfig = readConfig();
        envConfig = new HashMap<>();
    }

    // Method to get the singleton instance for a specific type
    public static Config getInstance(InstanceType type) {
        if (!instances.containsKey(type)) {
            // Set the file path based on the instance type
            String filePath;
            if (type == InstanceType.CREDENTIAL) {
                filePath = "src/main/java/config/credentials.yml";

            } else {
                filePath = "src/main/java/config/env.yml";
            }
            // Create and store the instance
            instances.put(type, new Config(filePath));
        }
        return instances.get(type);
    }

    public void loadEnvConfig(String section) {
        envConfig = (Map<String, Object>) allConfig.get(section);
    }

    public Map<String, Object> getEnvConfig() {
        return envConfig;
    }
    public Map<String, Object> getAllConfig() {
        return allConfig;
    }
    public Map<String, Object> readConfig() {
        return FileHandler.readYaml(configFilePath);
    }
}
