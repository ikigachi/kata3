package software.ulpgc.katas.service;

import software.ulpgc.katas.model.Metadata;
import software.ulpgc.katas.model.Videogame;
import software.ulpgc.katas.model.VideogameExport;
import software.ulpgc.katas.parser.CsvParser;
import software.ulpgc.katas.parser.VideogameParser;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class VideogameTransformer {
    public VideogameExport transform(List<String> lines) throws IOException {
        if (lines.isEmpty()) {
            return new VideogameExport(List.of(), new Metadata(0, 0, 0));
        }

        List<String> header = CsvParser.parse(lines.getFirst())
                .orElseThrow(() -> new IOException("Malformed CSV header."));
        if (!header.equals(List.of("identifier", "title", "creator", "date"))) {
            throw new IOException("Expected CSV columns: identifier,title,creator,date.");
        }

        List<Videogame> videogames = lines.stream()
                .skip(1)
                .map(VideogameParser::parse)
                .flatMap(Optional::stream)
                .toList();
        int total = lines.size() - 1;
        return new VideogameExport(videogames,
                new Metadata(total, videogames.size(), total - videogames.size()));
    }
}
