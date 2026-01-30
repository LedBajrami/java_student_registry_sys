package utils.export;

import java.util.Map;

public interface ExportableInterface {
    public String[] getCSVHeaders();

    public String[] getCSVRow();

    public Map<String, String> toJSONObject();

    public Map<String, String> toXMLObject();
}
