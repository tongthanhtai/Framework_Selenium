package utils.GoogleApi;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class GoogleApi {
    protected static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    protected static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    protected static final List<String> SCOPES = List.of(SheetsScopes.SPREADSHEETS, DriveScopes.DRIVE);
    private static final File DATA_STORE_DIR = new File(System.getProperty("user.home"), ".credentials/sheets.googleapis.com-java-quickstart");
    private static FileDataStoreFactory DATA_STORE_FACTORY;
    private static HttpTransport HTTP_TRANSPORT;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            System.out.println("Google transport exception:" + t.getMessage());
            System.exit(1);
        }
    }

    /**
     * Creates and returns an authorized Drive API client service.
     * Determines the authorization type (OAuth2 or Service Account) based on the JSON structure.
     *
     * @param credentialsPath The path to the credentials JSON file.
     * @return Authorized Drive API client.
     * @throws RuntimeException if there is an issue reading the credentials.
     */
    public static Drive getDriveService(String credentialsPath) {
        Path externalCredentialsPath = Paths.get(System.getProperty("user.dir"), credentialsPath);
        System.out.println("Looking for external credentials file at: " + externalCredentialsPath.toAbsolutePath());

        try (InputStream in = Files.exists(externalCredentialsPath, LinkOption.NOFOLLOW_LINKS)
                ? Files.newInputStream(externalCredentialsPath)
                : GoogleApi.class.getClassLoader().getResourceAsStream(
                credentialsPath.startsWith("/") ? credentialsPath.substring(1) : credentialsPath)) {

            if (in == null) {
                throw new IOException("Credentials file not found: " + credentialsPath);
            }

            byte[] credentialsData = in.readAllBytes();
            return isServiceAccount(credentialsData)
                    ? getServiceDriveAccount(credentialsData)
                    : getOAuthDriveAccount(credentialsData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Obtains OAuth credentials from the input stream.
     *
     * @param in the InputStream containing credentials.
     * @return the authorized Credential object.
     */
    public static Credential getOAuthCredentials(InputStream in) {
        try {
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder( HTTP_TRANSPORT,
                    GsonFactory.getDefaultInstance(),
                    clientSecrets,
                    Collections.singletonList(DriveScopes.DRIVE))
                    .setDataStoreFactory(DATA_STORE_FACTORY)
                    .setAccessType("offline")
                    .build();
            return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        } catch (GoogleJsonResponseException e) {
            handleJsonException(e);
            throw new RuntimeException("Authorization failed", e);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error obtaining credentials", e);
        }
    }

    /**
     * Handles the specific GoogleJsonResponseException.
     *
     * @param e the GoogleJsonResponseException object.
     */
    private static void handleJsonException(GoogleJsonResponseException e) {
        e.printStackTrace();
    }

    /**
     * Checks if the provided credentials data represents a service account.
     * Reads JSON to detect if "type" is "service_account".
     *
     * @param credentialsData Byte array containing the credentials JSON data.
     * @return true if the credentials are for a service account, false if they are for OAuth2.
     */
    private static boolean isServiceAccount(byte[] credentialsData) {
        JsonObject json = JsonParser.parseReader(new InputStreamReader(new ByteArrayInputStream(credentialsData))).getAsJsonObject();
        return json.has("type") && "service_account".equals(json.get("type").getAsString());
    }

    /**
     * Creates an authorized Drive API client for a service account.
     *
     * @param credentialsData Byte array containing the service account credentials JSON data.
     * @return Drive API client for a service account.
     * @throws IOException if there is an issue reading the credentials.
     */
    private static Drive getServiceDriveAccount(byte[] credentialsData) throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(credentialsData))
                .createScoped(SCOPES);
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Creates an authorized Drive API client using OAuth2.
     *
     * @param credentialsData Byte array containing the OAuth2 credentials JSON data.
     * @return Drive API client for OAuth2 authorization.
     */
    private static Drive getOAuthDriveAccount(byte[] credentialsData) {
        Credential credential = getOAuthCredentials(new ByteArrayInputStream(credentialsData));
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .setHttpRequestInitializer(setHttpTimeout(credential))
                .build();
    }

    /**
     * Adds a custom timeout to the HttpRequest.
     *
     * @param requestInitializer the request initializer.
     * @return the HttpRequestInitializer with custom timeouts.
     */
    private static HttpRequestInitializer setHttpTimeout(final HttpRequestInitializer requestInitializer) {
        return httpRequest -> {
            requestInitializer.initialize(httpRequest);
            httpRequest.setConnectTimeout(3 * 60000);  // 3 minutes connect timeout
            httpRequest.setReadTimeout(3 * 60000);     // 3 minutes read timeout
        };
    }
}
