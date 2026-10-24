package software.ulpgc.katas;

import software.ulpgc.katas.io.JsonWriter;
import software.ulpgc.katas.io.VideogameReader;
import software.ulpgc.katas.model.VideogameExport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        VideogameExport result;
        try (InputStream input = Main.class.getResourceAsStream("/videogames-example.csv")) {
            if (input == null) {
                throw new IOException("No se encuentra videogames-example.csv");
            }

            result = VideogameReader.loadFrom(input);
        }

        Path outputPath = Path.of("output.json");
        try (OutputStream output = Files.newOutputStream(outputPath)) {
            new JsonWriter().write(result, output);
        }

        System.out.println("JSON generado en: " + outputPath.toAbsolutePath());
        System.out.println(result.metadata());
    }
}
