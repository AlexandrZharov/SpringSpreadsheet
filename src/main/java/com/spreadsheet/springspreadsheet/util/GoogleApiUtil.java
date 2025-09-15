package com.spreadsheet.springspreadsheet.util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.spreadsheet.springspreadsheet.dto.IssueDto;
import com.spreadsheet.springspreadsheet.dto.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.services.sheets.v4.Sheets;

@Component
public class GoogleApiUtil {
    private static final Logger LOG = LoggerFactory.getLogger(GoogleApiUtil.class);

    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens/path";
    private static final String SHEET_ID = "1Wioe8temI1DTcI3K4jnyHPZbFUt-YjOatw6whJNasT8";
    private static final String RANGE = "A2:F";

    /**
     * Global instance of the scopes required by this quickstart.
     */
    private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    public List<IssueDto> getIssuesByStatus(final Status status) throws IOException, GeneralSecurityException {
        final var issues = getIssuesFromSheet();
        return issues.stream().filter(issue -> issue.status().equals(status)).toList();
    }

    public List<IssueDto> getIssuesFromSheet() throws GeneralSecurityException, IOException {
        final var valuesIt = getDataFromSheet().values().iterator();
        final var storeDataFromGoogleSheet = new ArrayList<IssueDto>();
        while (valuesIt.hasNext()) {
            final var row = valuesIt.next();
            final var id = row.get(0).toString();
            final var description = row.get(1).toString();
            final var parentId = row.get(2).toString();
            final var status = row.get(3).toString();
            final var createdAt = row.get(4).toString();
            final var updatedAt = row.size() == 6 ? row.get(5).toString() : "";
            storeDataFromGoogleSheet.add(new IssueDto(id, description, parentId, Status.valueOf(status), createdAt,
                    updatedAt));
        }
        return storeDataFromGoogleSheet;
    }

    public Map<Integer, List<Object>> getDataFromSheet() throws GeneralSecurityException, IOException {
        // Build a new authorized API client service.

        Sheets service = getSheetService();
        ValueRange response = service.spreadsheets().values().get(SHEET_ID, RANGE).execute();
        final var values = response.getValues();
        final var storeDataFromGoogleSheet = new HashMap<Integer, List<Object>>();
        if (values.isEmpty()) {
            System.out.println("No data found.");
            return Map.of();
        }
        for (var i = 0; i < values.size(); i++) {
            final var row = values.get(i);
            storeDataFromGoogleSheet.put(i, row);
        }
        return storeDataFromGoogleSheet;
    }

    private static Sheets getSheetService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new Sheets.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
                .setApplicationName(APPLICATION_NAME).build();
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GoogleApiUtil.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(
                        new java.io.File(System.getProperty("user.home"), TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline").build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public void updateIssue(final List<List<Object>> values)
            throws IOException, GeneralSecurityException {
        Sheets service = getSheetService();

        try {
            // Updates the values in the specified range.
            ValueRange body = new ValueRange()
                    .setValues(values);
            service.spreadsheets().values().update(SHEET_ID, RANGE, body)
                    .setValueInputOption("RAW")
                    .execute();
        } catch (GoogleJsonResponseException e) {
            LOG.error(e.getMessage());
        }
    }
}
