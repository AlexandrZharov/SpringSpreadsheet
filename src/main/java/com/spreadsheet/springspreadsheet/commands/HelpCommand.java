package com.spreadsheet.springspreadsheet.commands;

public class HelpCommand implements Command {
    @Override
    public void invoke() {
        System.out.println("Next commands are supported:");
        System.out.println("Type '1' to create a new issue");
        System.out.println("Type '2' to update an existing issue");
        System.out.println("Type '3' to list all issues");
        System.out.println("Type 'q' to exit application");
        System.out.println("Type 'help' to list all supported commands");
    }
}
