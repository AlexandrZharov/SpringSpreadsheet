package com.spreadsheet.springspreadsheet;

import com.spreadsheet.springspreadsheet.commands.*;
import com.spreadsheet.springspreadsheet.service.GoogleApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@SpringBootApplication
public class SpringSpreadsheetApplication implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(SpringSpreadsheetApplication.class);

    @Autowired
    private final GoogleApiService googleApiService = new GoogleApiService();

	public static void main(String[] args) {
        LOG.info("Starting Spring Spreadsheet Application");
		SpringApplication.run(SpringSpreadsheetApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {
        Map<String, Command> commands = new HashMap<>();
        commands.put("1", new CreateCommand(googleApiService));
        commands.put("2", new UpdateCommand(googleApiService));
        commands.put("3", new ListCommand(googleApiService));
        commands.put("q", new ExitCommand());
        commands.put("help", new HelpCommand());

        Scanner inScanner = new Scanner(System.in);
        System.out.println("Type 'help' for help or type action key to do action.");
        while (inScanner.hasNextLine()) {
            final String commandName = inScanner.nextLine().replaceAll("\n", "");
            Command cmd = commands.get(commandName);
            if (cmd != null) {
                cmd.invoke();
            } else {
                System.err.printf("Unknown command %s. Type 'help' to list all supported commands!\n", commandName);
            }
            System.out.println("Type next action: ");
        }
    }
}
