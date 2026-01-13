package commands;

public class CLICommandInterface {

    public static void displayStartingCommands() {
        System.out.println("-- Student Registry System --");
        System.out.println("Available commands:");
        System.out.println("  load <folder>           - Load data from folder");
        System.out.println("  find <entity> <key>     - Find a record");
        System.out.println("  query <entity> <criteria> - Query records");
        System.out.println("  add <entity> <values>   - Add new record");
        System.out.println("  report <type> <params>  - Generate report");
        System.out.println("  quit                    - Exit program");
        System.out.println();
    }
}
