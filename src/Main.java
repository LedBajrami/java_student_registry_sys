import commands.*;
import registry.RegistrySystem;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        RegistrySystem registrySystem = new RegistrySystem();

        CLICommandInterface.displayStartingCommands();
        long startTime = System.nanoTime();

        while (true) {
            System.out.print("? ");
            String input = scanner.nextLine().trim();
            String formattedInput = input.toLowerCase();
            String result = "";

            if (formattedInput.equals("quit")) {
                System.out.println("The program is terminated.");
                break;
            } else if (formattedInput.startsWith("load")) {
                result = LoadCommand.exec(input, registrySystem);
            } else if (!registrySystem.isDataLoaded()) {
                result = "Please load the data first! HINT: load <folderPath>";
            } else if (formattedInput.startsWith("find")) {
                result = FindCommand.exec(input, registrySystem);
            } else if (formattedInput.startsWith("query")) {
                result = QueryCommand.exec(input, registrySystem);
            } else if (formattedInput.startsWith("add")) {
                result = AddCommand.exec(input, registrySystem);
            } else if (formattedInput.startsWith("report")) {
                result = ReportCommand.exec(input, registrySystem);
            } else  {
                result = "Unknown command: " + input;
            }

            long endTime = System.nanoTime();
            long executionTimeMs = (endTime - startTime) / 1_000_000;

            System.out.println(result);
            System.out.println("Execution time: " + executionTimeMs + "ms");
        }

        scanner.close();
    }
}
