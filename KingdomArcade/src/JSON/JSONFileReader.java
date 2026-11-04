package JSON;

import java.nio.file.*;
import java.io.IOException;
import java.util.*;

public class JSONFileReader {
    private String json;
    private int index;

    // read JSON file
    public String readFile(String filePath) throws IOException{
        Path file_path = Path.of(filePath);

        if(!Files.exists(file_path)){
            throw new IOException("JSON file not found: " + file_path);
        }

        return Files.readString(file_path);
    }

    // parsing method;
    public Map<String, Object> parise(String json){
        this.json = json;
        this.index = 0;

        skipWhiteSpace();
        Map<String, Object> result = parseObject();
        skipWhiteSpace();

        if(index != json.length()){
            throw new IllegalArgumentException("Unexpected JSON content");
        }

        return result;
    }

    private void skipWhiteSpace(){
        while(index < json.length() && Character.isWhitespace(json.charAt(index))){
            index++;
        }
    }

    private char currentChar(){
        if(index >= json.length()){
            throw new IllegalArgumentException("Unexpected end of JSON");
        }

        return json.charAt(index);
    }

    private void expect(char expected){
        if(currentChar() != expected){
            throw new IllegalArgumentException("Expected '" + expected "' at position: " + index);
        }

        index++;
    }
}