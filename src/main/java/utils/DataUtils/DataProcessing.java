package utils.DataUtils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DataProcessing {

    public static JSONObject parseJsonFileToJsonObject(String jsonFilePath) throws IOException, ParseException {
        //Read existing JSON file with UTF-8 encoding
        String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)), "UTF-8");
        //Create JSONParser to prepare for parsing string content to JSONObject
        JSONParser jsonParser = new JSONParser();
        //Parse string content to JSONObject
        JSONObject jsonObject = (JSONObject) jsonParser.parse(content);
        System.out.println("parsed jsonObject: " + jsonObject);
        return jsonObject;
    }

    public static JSONObject getSpecificJsonObject(JSONObject originalObject, String key) {
        System.out.println("getSpecificJsonObject: " + originalObject.get(key));
        return (JSONObject) originalObject.get(key);
    }

    public static void updateObjectValue(JSONObject originalObject, String key, Object value) {
        originalObject.put(key, value);
    }
}
