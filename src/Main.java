import commands.CLICommandInterface;
import commands.LoadCommand;
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

            String result = "";

            if (input.startsWith("load")) {
                result = LoadCommand.exec(input, registrySystem);
            } else if (input.equals("quit")) {
                System.out.println("The program is terminated.");
                break;
            } else {
                result = "Unknown command: " + input;
            }

            System.out.println(result);
        }

        scanner.close();
    }
}