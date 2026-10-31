package software.ulpgc.katas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import software.ulpgc.katas.io.ArchiveVideogameReader;
import software.ulpgc.katas.io.CsvSource;
import software.ulpgc.katas.model.Metadata;
import software.ulpgc.katas.model.VideogameExport;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArchiveVideogameReaderTest {
    private static final String CSV = """
            "identifier","title","creator","date"
            "id","Example","Creator","2000-01-01T00:00:00Z"
            """;

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 10, 20})
    void sendsRequestedCountToTheServer(int count) throws IOException {
        CsvSource source = uri -> {
            assertEquals("https", uri.getScheme());
            assertEquals("archive.org", uri.getHost());
            assertEquals("/advancedsearch.php", uri.getPath());
            String query = URLDecoder.decode(uri.getRawQuery(), StandardCharsets.UTF_8);
            assertTrue(query.contains("&rows=" + count + "&"));
            assertTrue(query.contains("q=collection:softwarelibrary_msdos_games AND creator:* AND date:*"));
            assertTrue(query.contains("&fl[]=identifier&fl[]=title&fl[]=creator&fl[]=date&"));
            assertTrue(query.endsWith("&page=1&output=csv"));
            return response(CSV);
        };

        VideogameExport result = new ArchiveVideogameReader(source).read(count);

        assertEquals(new Metadata(1, 1, 0), result.metadata());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void rejectsInvalidCountsBeforeOpeningSource(int count) {
        CsvSource source = uri -> {
            throw new AssertionError("Invalid counts must not reach the API.");
        };
        assertThrows(IllegalArgumentException.class,
                () -> new ArchiveVideogameReader(source).read(count));
    }

    @Test
    void propagatesSourceErrors() {
        CsvSource source = uri -> {
            throw new IOException("Source unavailable");
        };
        IOException error = assertThrows(IOException.class,
                () -> new ArchiveVideogameReader(source).read(10));
        assertEquals("Source unavailable", error.getMessage());
    }

    @Test
    void closesOwnedStreamAfterSuccess() throws IOException {
        AtomicBoolean closed = new AtomicBoolean();
        CsvSource source = uri -> trackedResponse(CSV, closed);
        new ArchiveVideogameReader(source).read(10);
        assertTrue(closed.get());
    }

    @Test
    void closesOwnedStreamWhenResponseIsNotCsv() {
        AtomicBoolean closed = new AtomicBoolean();
        CsvSource source = uri -> trackedResponse("<html>Unavailable</html>", closed);
        assertThrows(IOException.class, () -> new ArchiveVideogameReader(source).read(10));
        assertTrue(closed.get());
    }

    private static InputStream response(String csv) {
        return new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
    }

    private static InputStream trackedResponse(String csv, AtomicBoolean closed) {
        return new FilterInputStream(response(csv)) {
            @Override
            public void close() throws IOException {
                closed.set(true);
                super.close();
            }
        };
    }
}
