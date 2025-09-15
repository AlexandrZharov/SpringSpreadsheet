package com.spreadsheet.springspreadsheet.commands;

import com.spreadsheet.springspreadsheet.service.GoogleApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateCommand implements Command {
    private static final Logger LOG = LoggerFactory.getLogger(CreateCommand.class);

    private final GoogleApiService googleApiService;

    public CreateCommand(final GoogleApiService googleApiService) {
        this.googleApiService = googleApiService;
    }

    @Override
    public void invoke() {

    }
}
