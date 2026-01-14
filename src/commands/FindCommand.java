package commands;

import registry.RegistrySystem;

public class FindCommand {
    public static String exec(String command, RegistrySystem registrySystem) {
        try {
            String[] commandParts = command.split(" ");

            if (commandParts.length < 3) {
                return "Please write the whole command! Correct command: find <entity> <key>";
            }

            String commandEntity = commandParts[1].trim().toLowerCase();
            String commandKey = commandParts[2].trim().toUpperCase();
            String result = "";

            switch (commandEntity) {
                case "course":
                    if (commandParts.length != 3) {
                        return "Please write the whole command! Correct command: find course <course_code>";
                    }
                    result = registrySystem.findCourse(commandKey);
                    break;
                case "student":
                    if (commandParts.length != 3) {
                        return "Please write the whole command! Correct command: find student <student_id>";
                    }
                    result = registrySystem.findStudent(commandKey);
                    break;
                case "grade":
                    if (commandParts.length < 4) {
                        return "Please write the whole command! Correct command: find grade <student_id, key>";
                    }
                    String studentId = commandParts[2].trim().replaceAll(",", "");
                    String courseCode = commandParts[3].trim().toUpperCase();
                    result = registrySystem.findGrade(studentId, courseCode);
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
