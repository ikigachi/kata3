package software.ulpgc.katas.service;

import software.ulpgc.katas.model.Metadata;
import software.ulpgc.katas.model.Movie;
import software.ulpgc.katas.model.MovieExport;
import software.ulpgc.katas.parser.CsvParser;
import software.ulpgc.katas.parser.MovieParser;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MovieTransformer {
    public MovieExport transform(List<String> lines) throws IOException {
        if (lines.isEmpty()) {
            return new MovieExport(List.of(), new Metadata(0, 0, 0));
        }

        List<String> header = CsvParser.parse(lines.getFirst())
                .orElseThrow(() -> new IOException("Malformed CSV header."));
        if (!header.equals(List.of("identifier", "title", "creator", "date"))) {
            throw new IOException("Expected CSV columns: identifier,title,creator,date.");
        }

        List<Movie> movies = lines.stream()
                .skip(1)
                .map(MovieParser::parse)
                .flatMap(Optional::stream)
                .toList();
        int total = lines.size() - 1;
        return new MovieExport(movies,
                new Metadata(total, movies.size(), total - movies.size()));
    }
}
