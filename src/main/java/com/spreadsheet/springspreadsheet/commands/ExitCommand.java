package com.spreadsheet.springspreadsheet.commands;

public class ExitCommand implements Command {
    @Override
    public void invoke() {
        System.exit(0);
    }
}
