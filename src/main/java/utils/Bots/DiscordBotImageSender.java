package utils.Bots;

import lombok.Getter;
import okhttp3.*;
import utils.FileUtil.Config;
import utils.PathUtil.PathUtil;

import java.io.File;
import java.util.Map;

public class DiscordBotImageSender {
    @Getter
    public static net.dv8tion.jda.api.JDA jda;

    public void sendImageToGroup(String pathImage) throws InterruptedException {

        Map<String, Object> credential = Config.getInstance(Config.InstanceType.CREDENTIAL).getEnvConfig();
        String WEBHOOK_URL = (String) credential.get("webhookUrl");

        String imagePath = PathUtil.normalizePath(pathImage);
        File imageFile = new File(imagePath);

        if (!imageFile.exists()) {
            System.err.println("Image file does not exist: " + imagePath);
            return;
        }

        if (imageFile.length() > 8 * 1024 * 1024) {
            System.err.println("File too large: " + imageFile.length() + " bytes");
            return;
        }

        try {
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", imageFile.getName(),
                            RequestBody.create(imageFile, MediaType.parse("image/png")))
                    .build();

            Request request = new Request.Builder()
                    .url(WEBHOOK_URL)
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    System.out.println("Webhook report sent successfully to: " + WEBHOOK_URL);
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
