package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class FileHandler {
    public static List<String> readLines(String filePath) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            return lines.toList();
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

