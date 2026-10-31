package software.ulpgc.katas;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import software.ulpgc.katas.io.JsonWriter;
import software.ulpgc.katas.io.VideogameReader;
import software.ulpgc.katas.model.Metadata;
import software.ulpgc.katas.model.Videogame;
import software.ulpgc.katas.model.VideogameExport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VideogamePipelineTest {
    private static final String HEADER = "rank,name,platform,yearOfRelease,genre,publisher,naSales,euSales,jpSales,otherSales,globalSales";
    private static final String VALID_ROW = "1,Portal 2,PC,2011,Puzzle,Valve,0,0,0,0,0";

    @TempDir
    Path temporaryDirectory;

    @Test
    void generatesExpectedJsonForExampleDataset() throws IOException {
        VideogameExport result = VideogameReader.loadFrom(
                new StringReader(resourceText("/videogames-example.csv"))
        );

        assertEquals(
                JsonParser.parseString(resourceText("/expected.json")),
                JsonParser.parseString(serialize(result))
        );
    }

    @Test
    void writesExpectedJsonToFile() throws IOException {
        VideogameExport result = VideogameReader.loadFrom(
                new StringReader(resourceText("/videogames-example.csv"))
        );
        Path output = temporaryDirectory.resolve("output.json");

        new JsonWriter().write(result, output);

        assertEquals(
                JsonParser.parseString(resourceText("/expected.json")),
                JsonParser.parseString(Files.readString(output, StandardCharsets.UTF_8))
        );
    }

    @Test
    void convertsValidRecordToDomainObject() throws IOException {
        VideogameExport result = VideogameReader.loadFrom(
                new StringReader(HEADER + "\n" + VALID_ROW)
        );
        Videogame expected = new Videogame(
                "1", "Portal 2", "PC", "2011", "Puzzle", "Valve",
                "0", "0", "0", "0", "0"
        );

        assertEquals(List.of(expected), result.videogames());
        assertEquals(new Metadata(1, 1, 0), result.metadata());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1,\"Unfinished,PC,2020,Puzzle,Publisher,0,0,0,0,0",
            "1,Ga\"me,PC,2020,Puzzle,Publisher,0,0,0,0,0",
            "1,\"Game\"unexpected,PC,2020,Puzzle,Publisher,0,0,0,0,0",
            "1,Incomplete",
            "1,Game,PC,2020,Puzzle,Publisher,0,0,0,0,0,extra",
            "1,,PC,2020,Puzzle,Publisher,0,0,0,0,0",
            "1,Game,,2020,Puzzle,Publisher,0,0,0,0,0",
            "1,Game,PC,,Puzzle,Publisher,0,0,0,0,0",
            "1,Game,PC,2020,   ,Publisher,0,0,0,0,0",
            "1,Game,PC,abc,Puzzle,Publisher,0,0,0,0,0",
            "1,Game,PC,0,Puzzle,Publisher,0,0,0,0,0",
            "1,Game,PC,-1,Puzzle,Publisher,0,0,0,0,0"
    })
    void discardsInvalidRecordAndContinuesReading(String invalidRow) throws IOException {
        String input = String.join("\n", HEADER, VALID_ROW, invalidRow, VALID_ROW);
        VideogameExport result = VideogameReader.loadFrom(new StringReader(input));

        assertEquals(List.of("Portal 2", "Portal 2"),
                result.videogames().stream().map(Videogame::name).toList());
        assertEquals(new Metadata(3, 2, 1), result.metadata());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", HEADER})
    void serializesEmptyDatasetWithZeroCounters(String input) throws IOException {
        VideogameExport result = VideogameReader.loadFrom(new StringReader(input));
        String expected = """
                {
                  "videogames": [],
                  "metadata": {
                    "totalRecords": 0,
                    "validRecords": 0,
                    "discardedRecords": 0
                  }
                }
                """;

        assertEquals(JsonParser.parseString(expected), JsonParser.parseString(serialize(result)));
    }

    @Test
    void preservesCommasAndEscapedQuotesInTitles() throws IOException {
        String input = String.join("\n", HEADER,
                "1,\"Game, Part Two\",PC,2020,Puzzle,Publisher,0,0,0,0,0",
                "2,\"Game \"\"A\"\"\",PC,2021,Puzzle,Publisher,0,0,0,0,0");

        VideogameExport result = VideogameReader.loadFrom(new StringReader(input));

        assertEquals(List.of("Game, Part Two", "Game \"A\""),
                result.videogames().stream().map(Videogame::name).toList());
        assertEquals(new Metadata(2, 2, 0), result.metadata());
    }

    @Test
    void generatedJsonCanBeParsedAgainWithSpecialCharacters() throws IOException {
        Videogame game = new Videogame(
                "1", "Juego \"A\"\nEspaña\\B", "PC", "2020", "Puzzle", null,
                null, null, null, null, null
        );
        VideogameExport expected = new VideogameExport(List.of(game), new Metadata(1, 1, 0));

        VideogameExport parsed = new Gson().fromJson(serialize(expected), VideogameExport.class);

        assertEquals(expected, parsed);
    }

    @Test
    void producesSameResultFromFileMemoryAndReader() throws IOException {
        String input = resourceText("/videogames-example.csv");
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        Path file = temporaryDirectory.resolve("videogames.csv");
        Files.write(file, bytes);

        VideogameExport fromFile;
        try (InputStream stream = Files.newInputStream(file)) {
            fromFile = VideogameReader.loadFrom(stream);
        }

        VideogameExport fromMemory;
        try (InputStream stream = new ByteArrayInputStream(bytes)) {
            fromMemory = VideogameReader.loadFrom(stream);
        }

        VideogameExport fromReader = VideogameReader.loadFrom(new StringReader(input));

        assertEquals(fromFile, fromMemory);
        assertEquals(fromFile, fromReader);
        assertEquals(new Metadata(3, 2, 1), fromFile.metadata());
    }

    private static String resourceText(String name) throws IOException {
        try (InputStream input = VideogamePipelineTest.class.getResourceAsStream(name)) {
            if (input == null) {
                throw new IOException("No se encuentra el recurso: " + name);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static String serialize(VideogameExport result) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        new JsonWriter().write(result, output);
        return output.toString(StandardCharsets.UTF_8);
    }
}
