package commands;

import registry.RegistrySystem;

import java.io.IOException;

public class LoadCommand {
    public static String exec(String command, RegistrySystem registrySystem) {
       try {
           String[] commandParts = command.split(" ");

           if (commandParts.length < 2) {
               return "Please also specify the folderPath! Correct command: load <folderPath>";
           }

           String filePath = commandParts[1];

           String result = registrySystem.loadFile(filePath);

           return result;
       } catch (IOException e) {
            return "Something went wrong while loading data: " + e.getMessage();
       } catch (Exception e) {
           return "Something went wrong: " + e.getMessage();
       }
    }
}
