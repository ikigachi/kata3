package software.ulpgc.katas.parser;

import software.ulpgc.katas.model.Videogame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class VideogameParser {
    public static Optional<Videogame> toVideogame(String line) {
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
        return toVideogame(fields.toArray(String[]::new));
    }

    public static Optional<Videogame> toVideogame(String[] fields) {
        if (fields == null || fields.length != 11) {
            return Optional.empty();
        }

        fields = Arrays.stream(fields)
                .map(field -> field == null || field.isBlank() ? null : field.strip())
                .toArray(String[]::new);

        if (fields[1] == null || fields[2] == null || fields[3] == null || fields[4] == null) {
            return Optional.empty();
        }

        try {
            if (Integer.parseInt(fields[3]) <= 0) {
                return Optional.empty();
            }
        } catch (NumberFormatException e) {
            return Optional.empty();
        }

        return Optional.of(new Videogame(
                fields[0],
                fields[1],
                fields[2],
                fields[3],
                fields[4],
                fields[5],
                fields[6],
                fields[7],
                fields[8],
                fields[9],
                fields[10]
        ));
    }
}
