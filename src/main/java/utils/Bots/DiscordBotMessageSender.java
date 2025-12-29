package utils.Bots;

import common.BaseTest;
import lombok.Getter;
import okhttp3.*;
import org.json.simple.JSONObject;
import utils.FileUtil.Config;

import java.io.IOException;
import java.util.Map;

public class DiscordBotMessageSender {
    @Getter
    public static net.dv8tion.jda.api.JDA jda;

    //Because this class is initialized before @BeforeTest => Cannot use attribute env in the annotation to test
    public static Config credential = Config.getInstance(Config.InstanceType.CREDENTIAL);
    private static String selectCredential = BaseTest.getSelectCredential();
    private static final String WEB_HOOK = (String) ((Map<String, Object>) credential.getAllConfig().get(selectCredential)).get("webhookUrl");

    public void sendMessageToGroupPC(String message) {
        try {
            OkHttpClient client = new OkHttpClient();

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("content", message);

            RequestBody requestBody = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(WEB_HOOK)
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    System.out.println("Webhook report sent successfully to: " + WEB_HOOK);
                } else {
                    System.out.println("Failed to send webhook. Status code: " + response.code());
                    System.out.println("Response body: " + response.body().string());
                }
            }
        } catch (Exception e) {
            System.out.println("Error sending webhook: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
