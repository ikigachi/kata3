package software.ulpgc.katas.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CsvParser {
    public static Optional<List<String>> parse(String line) {
        if (line == null) {
            return Optional.empty();
        }

        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean insideQuotes = false;
        boolean quoteClosed = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (insideQuotes) {
                if (character == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        field.append('"');
                        i++;
                    } else {
                        insideQuotes = false;
                        quoteClosed = true;
                    }
                } else {
                    field.append(character);
                }
            } else if (character == ',') {
                fields.add(field.toString());
                field.setLength(0);
                quoteClosed = false;
            } else if (character == '"') {
                if (!field.isEmpty() || quoteClosed) {
                    return Optional.empty();
                }
                insideQuotes = true;
            } else if (quoteClosed) {
                if (!Character.isWhitespace(character)) {
                    return Optional.empty();
                }
            } else {
                field.append(character);
            }
        }

        if (insideQuotes) {
            return Optional.empty();
        }
        fields.add(field.toString());
        return Optional.of(List.copyOf(fields));
    }
}
