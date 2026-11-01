package software.ulpgc.katas;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import software.ulpgc.katas.io.JsonWriter;
import software.ulpgc.katas.io.BookReader;
import software.ulpgc.katas.model.Metadata;
import software.ulpgc.katas.model.Book;
import software.ulpgc.katas.model.BookExport;

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

class BookPipelineTest {
    private static final String HEADER = "identifier,title,creator,date";
    private static final String VALID_ROW = "id,Example,Creator,2000-01-01T00:00:00Z";

    @TempDir
    Path temporaryDirectory;

    @Test
    void generatesExpectedJson() throws IOException {
        BookExport result = BookReader.loadFrom(
                new StringReader(resourceText("/books-example.csv")));
        assertEquals(JsonParser.parseString(resourceText("/expected.json")),
                JsonParser.parseString(serialize(result)));
    }

    @Test
    void convertsApiDateToDomainYear() throws IOException {
        BookExport result = BookReader.loadFrom(new StringReader(HEADER + "\n" + VALID_ROW));
        assertEquals(List.of(new Book("id", "Example", "Creator", 2000)), result.books());
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
        BookExport result = BookReader.loadFrom(new StringReader(input));
        assertEquals(2, result.books().size());
        assertEquals(new Metadata(3, 2, 1), result.metadata());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", HEADER})
    void serializesEmptyInputWithZeroCounters(String input) throws IOException {
        BookExport result = BookReader.loadFrom(new StringReader(input));
        String expected = """
                {"books":[],"metadata":{"totalRecords":0,"validRecords":0,"discardedRecords":0}}
                """;
        assertEquals(JsonParser.parseString(expected), JsonParser.parseString(serialize(result)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"title,identifier,creator,date", "identifier,title,creator", "\"unfinished"})
    void rejectsUnexpectedOrMalformedHeaders(String header) {
        assertThrows(IOException.class,
                () -> BookReader.loadFrom(new StringReader(header + "\n" + VALID_ROW)));
    }

    @Test
    void preservesQuotedCommasAndEscapedQuotes() throws IOException {
        String input = String.join("\n", HEADER,
                "id,\"Example, Part Two\",Creator,2000-01-01T00:00:00Z",
                "id2,\"Example \"\"A\"\"\",Creator,2000-01-01T00:00:00Z");
        BookExport result = BookReader.loadFrom(new StringReader(input));
        assertEquals(List.of("Example, Part Two", "Example \"A\""),
                result.books().stream().map(Book::title).toList());
    }

    @Test
    void jsonCanBeReadAgainWithSpecialCharacters() throws IOException {
        Book item = new Book("id", "Example \"A\"\nEspa\u00f1a\\B", "Creator", 2000);
        BookExport expected = new BookExport(List.of(item), new Metadata(1, 1, 0));
        assertEquals(expected, new Gson().fromJson(serialize(expected), BookExport.class));
    }

    @Test
    void producesEquivalentResultsFromFileMemoryAndReader() throws IOException {
        String input = resourceText("/books-example.csv");
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        Path file = temporaryDirectory.resolve("input.csv");
        Files.write(file, bytes);
        BookExport fromFile;
        try (InputStream stream = Files.newInputStream(file)) {
            fromFile = BookReader.loadFrom(stream);
        }
        BookExport fromMemory = BookReader.loadFrom(new ByteArrayInputStream(bytes));
        BookExport fromReader = BookReader.loadFrom(new StringReader(input));
        assertEquals(fromFile, fromMemory);
        assertEquals(fromFile, fromReader);
        assertEquals(new Metadata(3, 2, 1), fromFile.metadata());
    }

    @Test
    void writesExpectedJsonToFile() throws IOException {
        BookExport result = BookReader.loadFrom(
                new StringReader(resourceText("/books-example.csv")));
        Path output = temporaryDirectory.resolve("output.json");
        new JsonWriter().write(result, output);
        assertEquals(JsonParser.parseString(resourceText("/expected.json")),
                JsonParser.parseString(Files.readString(output, StandardCharsets.UTF_8)));
    }

    @Test
    void domainRejectsInvalidObjectsWithoutDependingOnParser() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book("", "Example", "Creator", 2000));
        assertThrows(IllegalArgumentException.class,
                () -> new Book("id", "Example", "Creator", 0));
    }

    @Test
    void exportProtectsItsCollection() {
        List<Book> items = new ArrayList<>(
                List.of(new Book("id", "Example", "Creator", 2000)));
        BookExport result = new BookExport(items, new Metadata(1, 1, 0));
        items.clear();
        assertEquals(1, result.books().size());
        assertThrows(UnsupportedOperationException.class, () -> result.books().clear());
    }

    @Test
    void metadataRejectsInconsistentCounts() {
        assertThrows(IllegalArgumentException.class, () -> new Metadata(3, 2, 2));
        assertThrows(IllegalArgumentException.class, () -> new Metadata(-1, 0, -1));
    }

    private static String resourceText(String name) throws IOException {
        try (InputStream input = BookPipelineTest.class.getResourceAsStream(name)) {
            if (input == null) {
                throw new IOException("Missing resource: " + name);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static String serialize(BookExport result) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        new JsonWriter().write(result, output);
        return output.toString(StandardCharsets.UTF_8);
    }
}
