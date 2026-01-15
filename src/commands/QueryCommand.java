package commands;

import registry.RegistrySystem;

public class QueryCommand {
    public static String exec(String command, RegistrySystem registrySystem) {
        try {
            // query course title=Introduction to Programming, code~CS
            String[] commandParts = command.split(" ", 3);
            if (commandParts.length < 3) {
                return "Please write the correct command! Correct command: query <entity> <field1><op (= or ~)><value1>, ..., <fieldN><op><valueN>";
            }

            String commandEntity = commandParts[1].trim().toLowerCase();
            String[] parametersArray = commandParts[2].split(",");

            String result = "";

            switch (commandEntity) {
                case "course":
                    result = registrySystem.queryCourse(parametersArray);
                    break;
                case "student":
                    result = registrySystem.queryStudent(parametersArray);
                    break;
                case "grade":
                    result = registrySystem.queryGrade(parametersArray);
                    break;
                default:
                    result = "no such entity";
                    break;
            }


            return result;
        } catch (Exception e) {
            return "Something went wrong: " + e.getMessage();
        }
    }
}