package dogukan.texttotableandjsonapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableRowData {
    private final List<String> headers = new ArrayList<>();
    private final List<String> columnData = new ArrayList<>();

    public void addColumnData(String header, String data) {
        headers.add(header);
        columnData.add(data);
    }

    public List<String> getHeaders() {
        return headers;
    }

    public String getColumnData(String header) {
        int index = headers.indexOf(header);
        return (index >= 0) ? columnData.get(index) : "";
    }

    public Map<String, String> getDataMap() {
        Map<String, String> dataMap = new HashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            dataMap.put(headers.get(i), columnData.get(i));
        }
        return dataMap;
    }
}
