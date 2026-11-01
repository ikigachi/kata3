package software.ulpgc.katas.parser;

import software.ulpgc.katas.model.Movie;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

public class MovieParser {
    public static Optional<Movie> parse(String line) {
        return CsvParser.parse(line).flatMap(MovieParser::parseFields);
    }

    private static Optional<Movie> parseFields(List<String> fields) {
        if (fields.size() != 4) {
            return Optional.empty();
        }

        List<String> values = fields.stream().map(String::strip).toList();
        try {
            int year = Instant.parse(values.get(3)).atOffset(ZoneOffset.UTC).getYear();
            return Optional.of(new Movie(values.get(0), values.get(1), values.get(2), year));
        } catch (DateTimeException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
