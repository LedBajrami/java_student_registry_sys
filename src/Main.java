import commands.*;
import registry.RegistrySystem;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    // TESTING MODE
    private static final Runtime runtime = Runtime.getRuntime();

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        RegistrySystem registrySystem = new RegistrySystem();

        CLICommandInterface.displayStartingCommands();

        // Display initial heap info
        displayHeapInfo();

        while (true) {
            System.out.print("? ");
            String input = scanner.nextLine().trim();
            String formattedInput = input.toLowerCase();
            String result = "";

            if (formattedInput.equals("quit")) {
                System.out.println("The program is terminated.");
                break;
            } else if (formattedInput.startsWith("load")) {
                long startTime = System.nanoTime();
                result = LoadCommand.exec(input, registrySystem);
                long endTime = System.nanoTime();
                result += "\n[Execution time: " + (endTime - startTime) / 1_000_000 + "ms]";
                result += getHeapUsage();
            } else if (!registrySystem.isDataLoaded()) {
                result = "Please load the data first! HINT: load <folder>";
            } else if (formattedInput.startsWith("find")) {
                long startTime = System.nanoTime();
                result = FindCommand.exec(input, registrySystem);
                long endTime = System.nanoTime();
                result += "\n[Execution time: " + (endTime - startTime) / 1_000_000 + "ms]";
                result += getHeapUsage();
            } else if (formattedInput.startsWith("query")) {
                long startTime = System.nanoTime();
                result = QueryCommand.exec(input, registrySystem);
                long endTime = System.nanoTime();
                result += "\n[Execution time: " + (endTime - startTime) / 1_000_000 + "ms]";
                result += getHeapUsage();
            } else if (formattedInput.startsWith("add")) {
                long startTime = System.nanoTime();
                result = AddCommand.exec(input, registrySystem);
                long endTime = System.nanoTime();
                result += "\n[Execution time: " + (endTime - startTime) / 1_000_000 + "ms]";
                result += getHeapUsage();
            } else if (formattedInput.startsWith("report")) {
                long startTime = System.nanoTime();
                result = ReportCommand.exec(input, registrySystem);
                long endTime = System.nanoTime();
                result += "\n[Execution time: " + (endTime - startTime) / 1_000_000 + "ms]";
                result += getHeapUsage();
            } else {
                result = "Unknown command: " + input;
            }

            System.out.println(result);
        }

        scanner.close();
    }

    private static String getHeapUsage() {
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long maxMemory = runtime.maxMemory() / (1024 * 1024);
        return "\n[Heap: " + usedMemory + "MB / " + maxMemory + "MB]";
    }

    private static void displayHeapInfo() {
        long maxMemory = runtime.maxMemory() / (1024 * 1024);
        System.out.println("Max heap size: " + maxMemory + "MB");
    }

     // PROD. MODE
//    public static void main(String[] args) throws IOException {
//        Scanner scanner = new Scanner(System.in);
//        RegistrySystem registrySystem = new RegistrySystem();
//
//        CLICommandInterface.displayStartingCommands();
//        long startTime = System.nanoTime();
//
//        while (true) {
//            System.out.print("? ");
//            String input = scanner.nextLine().trim();
//            String formattedInput = input.toLowerCase();
//            String result = "";
//
//            if (formattedInput.equals("quit")) {
//                System.out.println("The program is terminated.");
//                break;
//            } else if (formattedInput.startsWith("load")) {
//                result = LoadCommand.exec(input, registrySystem);
//            } else if (!registrySystem.isDataLoaded()) {
//                result = "Please load the data first! HINT: load <folder>";
//            } else if (formattedInput.startsWith("find")) {
//                result = FindCommand.exec(input, registrySystem);
//            } else if (formattedInput.startsWith("query")) {
//                result = QueryCommand.exec(input, registrySystem);
//            } else if (formattedInput.startsWith("add")) {
//                result = AddCommand.exec(input, registrySystem);
//            } else if (formattedInput.startsWith("report")) {
//                result = ReportCommand.exec(input, registrySystem);
//            } else  {
//                result = "Unknown command: " + input;
//            }
//
//            long endTime = System.nanoTime();
//            long executionTimeMs = (endTime - startTime) / 1_000_000;
//
//            System.out.println(result);
//            System.out.println("Execution time: " + executionTimeMs + "ms");
//        }
//
//        scanner.close();
//    }
}
