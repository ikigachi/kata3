package software.ulpgc.katas;

import software.ulpgc.katas.io.ArchiveVideogameReader;
import software.ulpgc.katas.io.JsonWriter;
import software.ulpgc.katas.io.VideogameReader;
import software.ulpgc.katas.io.UrlCsvSource;
import software.ulpgc.katas.model.VideogameExport;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        VideogameReader reader = new ArchiveVideogameReader(new UrlCsvSource());
        VideogameExport result = reader.read(10);
        new JsonWriter().write(result, Path.of("output.json"));
        System.out.println("JSON written to output.json");
        System.out.println(result.metadata());
    }
}
