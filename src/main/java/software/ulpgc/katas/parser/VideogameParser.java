package software.ulpgc.katas.parser;

import software.ulpgc.katas.model.Videogame;

import java.util.Arrays;
import java.util.Optional;

public class VideogameParser {
    public static Optional<Videogame> toVideogame(String line) {
        return line == null ? Optional.empty() : toVideogame(line.split(",", -1));
    }

    public static Optional<Videogame> toVideogame(String[] fields) {
        if (fields == null || fields.length != 11) {
            return Optional.empty();
        }

        fields = Arrays.stream(fields)
                .map(field -> field == null || field.isBlank() ? null : field.strip())
                .toArray(String[]::new);

        if (fields[1] == null || fields[2] == null
                || fields[3] == null || fields[4] == null) {
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
