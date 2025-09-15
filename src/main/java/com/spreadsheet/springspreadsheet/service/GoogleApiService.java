package com.spreadsheet.springspreadsheet.service;

import com.spreadsheet.springspreadsheet.dto.Status;
import com.spreadsheet.springspreadsheet.util.GoogleApiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;

@Service
public class GoogleApiService {

    @Autowired
    private GoogleApiUtil googleApiUtil;

    public void listDataFromGoogleSheet(final Status status) throws GeneralSecurityException, IOException {
        final var data = googleApiUtil.getIssuesByStatus(status);
        System.out.println("-------------------------------------------------------------------");
        System.out.println("| ID | DESCRIPTION | PARENT ID | STATUS | CREATED_AT | UPDATED_AT |");
        for (final var issue : data) {
            System.out.printf("| %s | %s | %s | %s | %s | %s |\n", issue.id(), issue.description(), issue.parentId(),
                    issue.status(), issue.createdAt(), issue.updatedAt());
        }
    }

    public void updateIssue(final String id, final Status status) throws GeneralSecurityException,
            IOException {
        final var data = googleApiUtil.getDataFromSheet();
        final var result = data.entrySet().stream().filter(row -> row.getValue().getFirst()
                .equals(id)).findFirst();
        if (result.isEmpty()) {
            System.err.println("Issue with id " + id + " not found");
        } else {
            final var index = result.get().getKey();
            var row = result.get().getValue();
            // update status
            row.set(3, status.toString());
            // update time
            if (row.size() == 5) {
                row.add(LocalDateTime.now().toString());
            } else {
                result.get().getValue().set(5, LocalDateTime.now().toString());
            }
            data.replace(index, row);
            googleApiUtil.updateIssue(data.values().stream().toList());
        }
    }
}
