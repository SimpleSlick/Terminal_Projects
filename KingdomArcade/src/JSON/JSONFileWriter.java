package JSON;

import java.nio.file.*;
import java.io.IOException;
import java.util.Map;

public class JSONFileWriter {
    // create JSON file
    public void createFile(String filePath) throws IOException{
        Path path = Path.of(filePath);

        // create parent directory if necessary
        Path parent = path.getParent();

        if(parent != null){
            Files.createDirectories(parent);
        }

        // create file if it does not exist
        if(Files.notExists(path)){
            Files.createFile(path);
        }
    }

    // write JSON data into files
    public void writeFile(String filePath, Map<String, Object> data) throws IOException{
        createFile(filePath);

        String json = formatJSON(data);

        Files.writeString(Path.of(filePath), json, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    // convert object to JSON
    public String formatJSON(Map<String, Object> data){
        StringBuilder result = new StringBuilder();
        formatObject(data, result, 0);
        return result.toString();
    }

    // format JSON Objects
    private void formatObject(Map<String, Object> data, StringBuilder result, int indentation){
        result.append("{\n");
        int count = 0;

        for(Map.Entry<String, Object> entry: data.entrySet()){
            indent(result, indentation + 1);

            result.append("\"").append(escapeString(entry.getKey())).append("\": ");
            formatValue(entry.getValue(), result, indentation + 1);
            count++;

            if(count < data.size()){
                result.append(",");
            }

            result.append("\n");
        }

        indent(result, indentation);
        result.append("}");
    }

    // determine value type
    private void formatValue(Object value, StringBuilder result, int indentation){
        if(value == null){
            result.append("null");
        } else if(value instanceof String){
            result.append("\"").append(escapeString((String) value)).append("\"");
        } else if(value instanceof Integer){
            result.append(value);
        } else if(value instanceof Map<?,?>){
            @SuppressWarnings("unchecked")
            Map<String, Object> nestedMap = (Map<String, Object>) value;

            formatObject(nestedMap, result, indentation);
        }else if(value instanceof Boolean){
            result.append(value);
        }else{
            throw new IllegalArgumentException("Unsupported Data Types: " + value.getClass().getName());
        }
    }

    // Escape special characters in string
    private String escapeString(String value){
        StringBuilder result = new StringBuilder();

        for(char c: value.toCharArray()){
            switch(c){
                case '"': result.append("\\\""); break;
                case '\\': result.append("\\\\"); break;
                case '\n': result.append("\\n"); break;
                case '\r': result.append("\\r"); break;
                case '\t': result.append("\\t"); break;
                case '\b': result.append("\\b"); break;
                case '\f': result.append("\\f"); break;

                default:
                    if(c < 0x20){
                        result.append(String.format("\\u%04x", (int) c));
                    }else{
                        result.append(c);
                    }
            }
        }

        return result.toString();
    }

    // Add indentation
    private void indent(StringBuilder result, int level){
        result.append("    ".repeat(level));
    }
}