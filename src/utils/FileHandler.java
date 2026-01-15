package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
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

    public static void appendLines(String filePath, String values) throws IOException {
        if (!Files.exists(Paths.get(filePath))) {
            throw new IOException("File does not exist: " + filePath);
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(values);
            bw.newLine();
        } catch (IOException e) {
            throw new IOException("Failed to append to file: " + filePath, e);
        }
    }
}

