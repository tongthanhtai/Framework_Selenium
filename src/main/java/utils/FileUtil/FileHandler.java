package utils.FileUtil;

import org.json.simple.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

public class FileHandler {

    public static Map<String, Object> readYaml(String yamlPath) {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = new FileInputStream(yamlPath)) {
            return yaml.load(inputStream);
        } catch (Exception e) {
            throw new IllegalArgumentException("Env file not found or cannot be loaded: " + yamlPath, e);
        }
    }

    public static void writeDataToJsonFile(String jsonFilePath, JSONObject jsonObject) {
        try (OutputStreamWriter file = new OutputStreamWriter(new FileOutputStream(jsonFilePath), "UTF-8")) {
            file.write(jsonObject.toString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
