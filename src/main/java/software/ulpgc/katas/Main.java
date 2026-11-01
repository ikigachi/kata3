package software.ulpgc.katas;

import software.ulpgc.katas.io.ArchiveMovieReader;
import software.ulpgc.katas.io.JsonWriter;
import software.ulpgc.katas.io.MovieReader;
import software.ulpgc.katas.io.UrlCsvSource;
import software.ulpgc.katas.model.MovieExport;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        MovieReader reader = new ArchiveMovieReader(new UrlCsvSource());
        MovieExport result = reader.read(10);
        new JsonWriter().write(result, Path.of("output.json"));
        System.out.println("JSON written to output.json");
        System.out.println(result.metadata());
    }
}
