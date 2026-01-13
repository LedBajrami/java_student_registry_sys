package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileHandler {
    public static List<String> readLines(String filePath) throws IOException {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));

            return lines;
        } catch (IOException e) {
            throw new IOException("Failed to read the file: " + filePath, e);
        }
    }

   // todo: implement the method to append lines to the files
}
