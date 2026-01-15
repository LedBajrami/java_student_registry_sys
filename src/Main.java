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
                result = "Please load the data first! HINT: load <folder>";
            } else if (formattedInput.startsWith("find")) {
                result = FindCommand.exec(input, registrySystem);
            } else if (formattedInput.startsWith("query")) {
                result = QueryCommand.exec(input, registrySystem);
            } else if (formattedInput.startsWith("add")) {
                result = AddCommand.exec(input, registrySystem);
            } else  {
                result = "Unknown command: " + input;
            }

            System.out.println(result);
        }

        scanner.close();
    }
}