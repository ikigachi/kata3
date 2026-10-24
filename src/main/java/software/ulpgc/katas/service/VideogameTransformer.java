package software.ulpgc.katas.service;

import software.ulpgc.katas.model.Metadata;
import software.ulpgc.katas.model.Videogame;
import software.ulpgc.katas.model.VideogameExport;
import software.ulpgc.katas.parser.VideogameParser;

import java.util.List;
import java.util.Optional;

public class VideogameTransformer {

    public VideogameExport transform(List<String> lines) {
        List<Videogame> videogames = lines.stream()
                .skip(1)
                .map(VideogameParser::toVideogame)
                .flatMap(Optional::stream)
                .toList();

        int totalRecords = Math.max(lines.size() - 1, 0);
        int validRecords = videogames.size();
        int discardedRecords = totalRecords - validRecords;

        Metadata metadata = new Metadata(
                totalRecords,
                validRecords,
                discardedRecords
        );

        return new VideogameExport(videogames, metadata);
    }
}