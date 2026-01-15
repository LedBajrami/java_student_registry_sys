package commands;

import registry.RegistrySystem;

public class AddCommand {
    public static String exec(String command, RegistrySystem registrySystem) {
        try {
            // query course title=Introduction to Programming, code~CS
            String[] commandParts = command.split(" ", 3);
            if (commandParts.length < 3) {
                return "Please write the correct command! Correct command: add <entity> <field1, field2,...>";
            }

            String commandEntity = commandParts[1].trim().toLowerCase();
            String[] parametersArray = commandParts[2].split(",");

            String result = "";

            switch (commandEntity) {
                case "course":
                    result = registrySystem.addCourse(parametersArray);
                    break;
                case "student":
                    result = registrySystem.addStudent(parametersArray);
                    break;
                case "grade":
                    result = registrySystem.addGrade(parametersArray);
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
