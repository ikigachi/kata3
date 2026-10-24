package software.ulpgc.katas.io;

import software.ulpgc.katas.model.Videogame;
import software.ulpgc.katas.parser.VideogameParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface VideogameReader {
    List<Videogame> readAll();

    static List<Videogame> loadFrom(InputStream inputStream) throws IOException {
        return loadFrom(new InputStreamReader(inputStream));
    }

    static List<Videogame> loadFrom(Reader reader) throws IOException {
        return loadFrom(reader.readAllLines());
    }

    static List<Videogame> loadFrom(List<String> lines) {
        return lines.stream()
                .skip(1)
                .map(VideogameParser::toVideogame)
                .flatMap(Optional::stream)
                .toList();
    }

}
