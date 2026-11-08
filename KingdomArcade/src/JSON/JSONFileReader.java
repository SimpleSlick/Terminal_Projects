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
    public Map<String, Object> parse(String json){
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

    // parse json objects
    private Map<String, Object> parseObject(){
        Map<String, Object> data = new LinkedHashMap<>();

        expect('{');
        skipWhiteSpace();

        if(currentChar() == '}'){
            index++;
            return data;
        }

        while(index < json.length()){
            skipWhiteSpace();

            String key = parseString();

            skipWhiteSpace();
            expect(':');

            Object value = parseValue();
            data.put(key, value);
            skipWhiteSpace();

            if(currentChar() == '}'){
                index++;
                return data;
            }

            expect(',');
        }
        throw new IllegalArgumentException("Unclosed JSON object");
    }

    private Object parseValue() {
        skipWhiteSpace();

        char current = currentChar();

        if(current == '{'){
            return parseObject();
        }

        if(current == '['){
            return parseArray();
        }

        if(current == '"'){
            return parseString();
        }

        if(current == '-' || Character.isDigit(current)){
            return parseInteger();
        }

        if(json.startsWith("true", index)){
            index += 4;
            return true;
        }

        if(json.startsWith("false", index)){
            index += 5;
            return false;
        }

        if(json.startsWith("null", index)){
            index += 4;
            return null;
        }

        throw new IllegalArgumentException("Unsupported JSON value");
    }

    private Integer parseInteger() {
        int start = index;

        if(currentChar() == '-'){
            index++;
        }

        while(index < json.length() && Character.isDigit(currentChar())){
            index++;
        }

        String number = json.substring(start, index);

        try{
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid Integer: " + number);
        }
    }

    private String parseString() {
        expect('"');

        StringBuilder result = new StringBuilder();

        while(index <= json.length()){
            char current = json.charAt(index++);

            if(current == '"'){
                return result.toString();
            }

            if(current == '\\'){
                if(index >= json.length()){
                    throw new IllegalArgumentException("Invalid escape sequence");
                }

                char escaped = json.charAt(index++);

                switch (escaped){
                    case '"': result.append('"'); break;
                    case '\\': result.append('\\'); break;
                    case '/': result.append('/'); break;
                    case 'n': result.append('\n'); break;
                    case 't': result.append('\t'); break;
                    case 'r': result.append('\r'); break;
                    case 'b': result.append('\b'); break;
                    case 'f': result.append('\f'); break;

                    default:
                        throw new IllegalArgumentException("Invalid escape Sequence");
                }
            }else{
                result.append(current);
            }
        }

        throw new IllegalArgumentException("Unclosed JSON String");
    }

    private List<Object> parseArray(){
        List<Object> data = new ArrayList<>();

        expect('[');
        skipWhiteSpace();

        if(currentChar() == ']'){
            index++;
            return data;
        }

        while(index < json.length()){
            Object value = parseValue();
            data.add(value);

            skipWhiteSpace();

            if(currentChar() == ']'){
                index++;
                return data;
            }

            expect(',');
        }

        throw new IllegalArgumentException("Unclosed JSON Array");
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
            throw new IllegalArgumentException("Expected '" + expected + "' at position: " + index);
        }

        index++;
    }
}