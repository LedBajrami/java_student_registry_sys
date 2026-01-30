package utils.export;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExportFileHandler {
    public <T extends ExportableInterface> String exportToCSV(List<T> data, String filePath) throws IOException {
        String[] csvHeaders = data.get(0).getCSVHeaders();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            // write the headers
            bw.write(String.join(",", csvHeaders));

            bw.newLine();

            // write the rows
            for (T entry : data) {
                String[] csvRowData = entry.getCSVRow();
                bw.write(String.join(",", csvRowData));
                bw.newLine();
            }

            return "report generated";
        } catch (IOException e) {
            throw new IOException("Failed to generate the report: " + filePath, e);
        }
    }

    public <T extends ExportableInterface> String exportToJSON(List<T> data, String filePath) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("[");

            for (int i = 0; i < data.size(); i++) {
                // for json we need to not add "," comma, to the final object {}, and final field(row) inside the object
                Map<String, String> jsonFormattedObject = data.get(i).toJSONObject();

                bw.write("\n\t{");
                // object count is represented by i
                // count the field num
                int fieldCount = 0;
                for (Map.Entry<String, String> entry : jsonFormattedObject.entrySet()) {
                    bw.write("\n\t\t\"" + entry.getKey() + "\": \"" + entry.getValue() + "\"");
                    if (fieldCount < jsonFormattedObject.size() - 1) {
                        bw.write(",");
                    }
                    fieldCount++;
                }

                bw.write("\n\t}");

                if (i < data.size() - 1) {
                    bw.write(",");
                }
            }
            bw.write("\n]");

            return "report generated";
        } catch (IOException e) {
            throw new IOException("Failed to generate the report: " + filePath, e);
        }
    }

    public <T extends ExportableInterface> String exportToXML(List<T> data, String filePath) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            bw.newLine();
            bw.write("<data>");

            for (T entry : data) {
                bw.write("\n\t<entry>");
                Map<String, String> xmlFormattedObject = entry.toXMLObject();

                for (Map.Entry<String, String> field : xmlFormattedObject.entrySet()) {
                    bw.write("\n\t\t<" + field.getKey() + ">" + field.getValue() + "</" + field.getKey() + ">");
                }

                bw.write("\n\t</entry>");
            }

            bw.write("\n</data>");

            return "report generated";
        } catch (IOException e) {
            throw new IOException("Failed to generate the report: " + filePath, e);
        }
    }

    public <T extends ExportableInterface> String exportTo(List<T> data, String fileName) throws IOException {
        Path folder = Paths.get("reportData"); // we check if there is an already existent folder "reportData" if not we create it automatically for smoother user experience
        Files.createDirectories(folder);

        String filePath = "reportData/" + fileName;


        try {
            if (fileName.endsWith(".csv")) {
                return exportToCSV(data, filePath);
            } else if (fileName.endsWith(".json")) {
                return exportToJSON(data, filePath);
            } else if (fileName.endsWith(".xml")) {
                return exportToXML(data, filePath);
            } else {
                return "error: unsupported file format! Please specify the format in file name (.csv, .json, .xml)";
            }
        } catch (IOException e) {
            throw new IOException("Failed to generate the report: " + filePath, e);
        }
    }
}
