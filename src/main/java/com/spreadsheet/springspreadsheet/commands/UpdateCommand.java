package com.spreadsheet.springspreadsheet.commands;

import com.spreadsheet.springspreadsheet.dto.Status;
import com.spreadsheet.springspreadsheet.service.GoogleApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Scanner;

public class UpdateCommand implements Command {
    private static final Logger LOG = LoggerFactory.getLogger(UpdateCommand.class);

    private final GoogleApiService googleApiService;

    public UpdateCommand(final GoogleApiService googleApiService) {
        this.googleApiService = googleApiService;
    }

    @Override
    public void invoke() {
        final var inScanner = new Scanner(System.in);
        System.out.println("Type issue ID:");
        final String id = inScanner.nextLine().replaceAll("\n", "");
        System.out.println("Type new status:");
        final String statusString = inScanner.nextLine().replaceAll("\n", "");

        try {
            final var status = Status.valueOf(statusString);
            googleApiService.updateIssue(id, status);
            System.out.println("Successfully updated issue with ID: " + id);
        } catch (GeneralSecurityException | IOException e) {
            LOG.error("Error during listing data from table: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid status string: " + statusString);
            System.err.println("Supported status strings: " + Arrays.toString(Status.values()));
        }
    }
}
