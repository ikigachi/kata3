package software.ulpgc.katas;

import software.ulpgc.katas.io.JsonWriter;
import software.ulpgc.katas.io.UrlVideogameReader;
import software.ulpgc.katas.io.VideogameReader;
import software.ulpgc.katas.model.VideogameExport;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        VideogameReader reader = new UrlVideogameReader(
                URI.create("https://api.github.com/repos/algorithms-and-data-representation/Real-World-Data-Visualization/contents/data/vgsales.csv").toURL(),
                Map.of("Accept", "application/vnd.github.raw+json", "User-Agent", "kata3")
        );
        VideogameExport result = reader.readAll();

        Path outputPath = Path.of("output.json");
        new JsonWriter().write(result, outputPath);

        System.out.println("JSON written to: " + outputPath.toAbsolutePath());
        System.out.println(result.metadata());
    }
}
