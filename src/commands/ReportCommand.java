package commands;

import registry.RegistrySystem;

public class ReportCommand {
    public static String exec(String command, RegistrySystem registrySystem) {
        try {
            String[] commandParts = command.split(" ", 4);
            if (commandParts.length < 4) {
                return "Please write the correct command! Correct command: report <type> ( <n> or <id> ) <filename>";
            }

            String studentId = null; // for transcript <id>
            int value = 0;           // for top courses and students <n>


            String commandType = commandParts[1].trim().toLowerCase();
            if (commandType.equals("transcript")) {
                studentId = commandParts[2].trim();
            } else {
                value = Integer.parseInt(commandParts[2].trim());
            }
            String fileName =  commandParts[3].trim();

            String result = "";

            switch (commandType) {
                case "topcourses":
                    if (value <= 0 || value > 100) {
                        return "Please provide a valid value. 1-100";
                    }
                    result = registrySystem.reportTopCourses(value, fileName);
                    break;
                case "beststudents":
                    if (value <= 0 || value > 100) {
                        return "Please provide a valid value. 1-100";
                    }
                    result = registrySystem.reportTopStudents(value, fileName);
                    break;
                case "transcript":
                    result = registrySystem.reportTranscript(studentId, fileName);
                    break;
                default:
                    result = "please provide a valid report type - (bestStudents, topCourses, transcript)";
                    break;
            }


            return result;
        } catch (Exception e) {
            return "Something went wrong: " + e.getMessage();
        }
    }
}