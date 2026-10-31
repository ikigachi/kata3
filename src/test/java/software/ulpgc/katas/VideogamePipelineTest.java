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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VideogamePipelineTest {
    private static final String HEADER = "identifier,title,creator,date";
    private static final String VALID_ROW = "id,Example,Creator,2000-01-01T00:00:00Z";

    @TempDir
    Path temporaryDirectory;

    @Test
    void generatesExpectedJson() throws IOException {
        VideogameExport result = VideogameReader.loadFrom(
                new StringReader(resourceText("/videogames-example.csv")));
        assertEquals(JsonParser.parseString(resourceText("/expected.json")),
                JsonParser.parseString(serialize(result)));
    }

    @Test
    void convertsApiDateToDomainYear() throws IOException {
        VideogameExport result = VideogameReader.loadFrom(new StringReader(HEADER + "\n" + VALID_ROW));
        assertEquals(List.of(new Videogame("id", "Example", "Creator", 2000)), result.videogames());
        assertEquals(new Metadata(1, 1, 0), result.metadata());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "id,Incomplete",
            "id,Example,Creator,2000-01-01T00:00:00Z,extra",
            ",Example,Creator,2000-01-01T00:00:00Z",
            "id,,Creator,2000-01-01T00:00:00Z",
            "id,Example,   ,2000-01-01T00:00:00Z",
            "id,Example,Creator,",
            "id,Example,Creator,invalid-date",
            "id,Example,Creator,2000-02-30T00:00:00Z",
            "id,Example,Creator,0000-01-01T00:00:00Z",
            "id,\"Unfinished,Creator,2000-01-01T00:00:00Z",
            "id,Exa\"mple,Creator,2000-01-01T00:00:00Z",
            "id,\"Example\"unexpected,Creator,2000-01-01T00:00:00Z"
    })
    void discardsInvalidRowsAndContinues(String invalidRow) throws IOException {
        String input = String.join("\n", HEADER, VALID_ROW, invalidRow, VALID_ROW);
        VideogameExport result = VideogameReader.loadFrom(new StringReader(input));
        assertEquals(2, result.videogames().size());
        assertEquals(new Metadata(3, 2, 1), result.metadata());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", HEADER})
    void serializesEmptyInputWithZeroCounters(String input) throws IOException {
        VideogameExport result = VideogameReader.loadFrom(new StringReader(input));
        String expected = """
                {"videogames":[],"metadata":{"totalRecords":0,"validRecords":0,"discardedRecords":0}}
                """;
        assertEquals(JsonParser.parseString(expected), JsonParser.parseString(serialize(result)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"title,identifier,creator,date", "identifier,title,creator", "\"unfinished"})
    void rejectsUnexpectedOrMalformedHeaders(String header) {
        assertThrows(IOException.class,
                () -> VideogameReader.loadFrom(new StringReader(header + "\n" + VALID_ROW)));
    }

    @Test
    void preservesQuotedCommasAndEscapedQuotes() throws IOException {
        String input = String.join("\n", HEADER,
                "id,\"Example, Part Two\",Creator,2000-01-01T00:00:00Z",
                "id2,\"Example \"\"A\"\"\",Creator,2000-01-01T00:00:00Z");
        VideogameExport result = VideogameReader.loadFrom(new StringReader(input));
        assertEquals(List.of("Example, Part Two", "Example \"A\""),
                result.videogames().stream().map(Videogame::name).toList());
    }

    @Test
    void jsonCanBeReadAgainWithSpecialCharacters() throws IOException {
        Videogame item = new Videogame("id", "Example \"A\"\nEspa\u00f1a\\B", "Creator", 2000);
        VideogameExport expected = new VideogameExport(List.of(item), new Metadata(1, 1, 0));
        assertEquals(expected, new Gson().fromJson(serialize(expected), VideogameExport.class));
    }

    @Test
    void producesEquivalentResultsFromFileMemoryAndReader() throws IOException {
        String input = resourceText("/videogames-example.csv");
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        Path file = temporaryDirectory.resolve("input.csv");
        Files.write(file, bytes);
        VideogameExport fromFile;
        try (InputStream stream = Files.newInputStream(file)) {
            fromFile = VideogameReader.loadFrom(stream);
        }
        VideogameExport fromMemory = VideogameReader.loadFrom(new ByteArrayInputStream(bytes));
        VideogameExport fromReader = VideogameReader.loadFrom(new StringReader(input));
        assertEquals(fromFile, fromMemory);
        assertEquals(fromFile, fromReader);
        assertEquals(new Metadata(3, 2, 1), fromFile.metadata());
    }

    @Test
    void writesExpectedJsonToFile() throws IOException {
        VideogameExport result = VideogameReader.loadFrom(
                new StringReader(resourceText("/videogames-example.csv")));
        Path output = temporaryDirectory.resolve("output.json");
        new JsonWriter().write(result, output);
        assertEquals(JsonParser.parseString(resourceText("/expected.json")),
                JsonParser.parseString(Files.readString(output, StandardCharsets.UTF_8)));
    }

    @Test
    void domainRejectsInvalidObjectsWithoutDependingOnParser() {
        assertThrows(IllegalArgumentException.class,
                () -> new Videogame("", "Example", "Creator", 2000));
        assertThrows(IllegalArgumentException.class,
                () -> new Videogame("id", "Example", "Creator", 0));
    }

    @Test
    void exportProtectsItsCollection() {
        List<Videogame> items = new ArrayList<>(
                List.of(new Videogame("id", "Example", "Creator", 2000)));
        VideogameExport result = new VideogameExport(items, new Metadata(1, 1, 0));
        items.clear();
        assertEquals(1, result.videogames().size());
        assertThrows(UnsupportedOperationException.class, () -> result.videogames().clear());
    }

    @Test
    void metadataRejectsInconsistentCounts() {
        assertThrows(IllegalArgumentException.class, () -> new Metadata(3, 2, 2));
        assertThrows(IllegalArgumentException.class, () -> new Metadata(-1, 0, -1));
    }

    private static String resourceText(String name) throws IOException {
        try (InputStream input = VideogamePipelineTest.class.getResourceAsStream(name)) {
            if (input == null) {
                throw new IOException("Missing resource: " + name);
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
