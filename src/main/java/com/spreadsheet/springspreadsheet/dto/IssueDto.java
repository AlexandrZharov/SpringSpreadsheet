package com.spreadsheet.springspreadsheet.dto;

import static java.util.Objects.requireNonNull;

public record IssueDto(String id, String description, String parentId, Status status, String createdAt,
                       String updatedAt) {
    public IssueDto {
        requireNonNull(id);
        requireNonNull(description);
        requireNonNull(status);
        requireNonNull(createdAt);
    }
}
