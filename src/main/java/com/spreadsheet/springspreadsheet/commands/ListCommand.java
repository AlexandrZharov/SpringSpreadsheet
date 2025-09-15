package com.spreadsheet.springspreadsheet.commands;

import com.spreadsheet.springspreadsheet.dto.Status;
import com.spreadsheet.springspreadsheet.service.GoogleApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Scanner;

public class ListCommand implements Command {
    private static final Logger LOG = LoggerFactory.getLogger(ListCommand.class);

    private final GoogleApiService googleApiService;

    public ListCommand(final GoogleApiService googleApiService) {
        this.googleApiService = googleApiService;
    }

    @Override
    public void invoke() {
        final var inScanner = new Scanner(System.in);
        LOG.info("Start listing all data from issues board");
        System.out.println("Type issue status to list issues:");
        final String statusString = inScanner.nextLine().replaceAll("\n", "");

        try {
            final var status = Status.valueOf(statusString);
            System.out.println("Listing all data from issues board");
            googleApiService.listDataFromGoogleSheet(status);
        } catch (GeneralSecurityException | IOException e) {
            LOG.error("Error during listing data from table: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid status string: " + statusString);
            System.err.println("Supported status strings: " + Arrays.toString(Status.values()));
        }
    }
}
