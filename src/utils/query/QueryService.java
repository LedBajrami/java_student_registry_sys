package utils.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueryService {

    public <T> String query(Collection<T> data, String[] parametersArray, FieldExtractor<T> fieldExtractor) {
        ArrayList<T> foundData = new ArrayList<>();

        for (T entityDataEntry : data) {
            boolean matches = true;

            for (String parameter : parametersArray) {

                if (parameter.contains("=")) {
                    String[] commandParts = parameter.split("=");
                    String key = commandParts[0].trim();
                    String value = commandParts[1].trim();

                    if (!value.equals(fieldExtractor.getField(entityDataEntry, key))) matches = false;
                } else  if (parameter.contains("~")) {
                    String[] commandParts = parameter.split("~");
                    String key = commandParts[0].trim();
                    String value = commandParts[1].trim();

                    String fieldValue = fieldExtractor.getField(entityDataEntry, key);
                    if (fieldValue == null || !fieldValue.contains(value)) matches = false;
                }
            }

            if (matches) foundData.add(entityDataEntry);
        }

        StringBuilder dataString = new StringBuilder();
        for (T entityEntryData : foundData) {
            dataString.append(entityEntryData.toString()).append("\n");
        }

        return String.format("%d records found\n%s", foundData.size(), dataString);
    }
}
