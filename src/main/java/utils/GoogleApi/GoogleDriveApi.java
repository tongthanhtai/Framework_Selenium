package utils.GoogleApi;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import lombok.Getter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Getter
public class GoogleDriveApi {
    protected final Drive drive;

    public GoogleDriveApi(String credentialsPath) {
        this.drive = GoogleApi.getDriveService(credentialsPath);
    }

    /**
     * Creates a folder with the specified name under the provided parent folder.
     *
     * @param name     The name of the folder to create.
     * @param parentId The ID of the parent folder.
     * @return The ID of the newly created folder.
     */
    public String createSubFolder(String name, String parentId) {
        File fileMetadata = new File();
        fileMetadata.setName(name);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        List<String> parents = List.of(parentId);
        fileMetadata.setParents(parents);
        File file;
        try {
            file = drive.files().create(fileMetadata)
                    .setFields("id")
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file.getId();
    }

    /**
     * Uploads a file to a specified Google Drive folder.
     *
     * @param pathFile The local file path of the image to upload.
     * @param folderId  The ID of the folder to upload the file to.
     * @return String return File ID if the upload is successful.
     */
    public String uploadFile(String pathFile, String folderId) {
        File fileMetadata = new File();
        String fileName = new java.io.File(pathFile).getName();
        String timestamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        String fileNameWithTimestamp = fileName.replace(".html", "_" + timestamp + ".html");

        System.out.println("Full Path: " + pathFile);
        System.out.println("File Exists: " + new java.io.File(pathFile).exists());
        fileMetadata.setName(fileNameWithTimestamp);
        fileMetadata.setParents(Collections.singletonList(folderId));

        java.io.File filePath = new java.io.File(pathFile);
        // 📌 Xác định MIME type theo đuôi file
        String mimeType = getMimeType(pathFile);  // Tự động xác định video/image
        FileContent mediaContent = new FileContent(mimeType, filePath);

        Drive.Files.Create fileCreate;

        try {
            fileCreate = drive.files().create(fileMetadata, mediaContent);
            fileCreate.setFields("id");
            File file = fileCreate.execute();
            return "https://drive.usercontent.google.com/u/0/uc?id=" + file.getId() + "&export=download";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getMimeType(String filePath) {
        String lower = filePath.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".mp4")) return "video/mp4";
        if (lower.endsWith(".mov")) return "video/quicktime";
        if (lower.endsWith(".avi")) return "video/x-msvideo";
        if (lower.endsWith(".mkv")) return "video/x-matroska";
        if (lower.endsWith(".html")) return "text/html";

        // ... thêm nếu cần
        return "application/octet-stream"; // fallback
    }
}