package software.ulpgc.katas;

import org.junit.jupiter.api.Test;
import software.ulpgc.katas.io.UrlVideogameReader;
import software.ulpgc.katas.io.VideogameReader;
import software.ulpgc.katas.model.Metadata;
import software.ulpgc.katas.model.VideogameExport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UrlVideogameReaderTest {

    @Test
    void sendsConfiguredHeadersAndReadsCsv() throws IOException {
        byte[] csv = exampleCsv();
        URL url = URL.of(URI.create("https://example.invalid/videogames"), new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL url) {
                return new URLConnection(url) {
                    @Override
                    public void connect() {
                    }

                    @Override
                    public InputStream getInputStream() {
                        assertEquals("application/vnd.github.raw+json", getRequestProperty("Accept"));
                        assertEquals("kata3", getRequestProperty("User-Agent"));
                        assertEquals(10000, getConnectTimeout());
                        assertEquals(10000, getReadTimeout());
                        return new ByteArrayInputStream(csv);
                    }
                };
            }
        });
        Map<String, String> headers = new HashMap<>(Map.of(
                "Accept", "application/vnd.github.raw+json", "User-Agent", "kata3"
        ));
        VideogameReader reader = new UrlVideogameReader(url, headers);
        headers.clear();

        VideogameExport result = reader.readAll();

        assertEquals(new Metadata(3, 2, 1), result.metadata());
    }

    @Test
    void supportsUrlsWithoutCustomHeaders() throws IOException {
        byte[] csv = exampleCsv();
        URL url = csvUrl(csv);

        VideogameExport result = new UrlVideogameReader(url).readAll();
        VideogameExport expected = VideogameReader.loadFrom(new ByteArrayInputStream(csv));

        assertEquals(expected, result);
    }

    @Test
    void propagatesSourceFailures() throws IOException {
        URL url = URL.of(URI.create("https://example.invalid/unavailable"), new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL url) throws IOException {
                throw new IOException("Source unavailable");
            }
        });

        IOException error = assertThrows(IOException.class,
                () -> new UrlVideogameReader(url).readAll());

        assertEquals("Source unavailable", error.getMessage());
    }

    private static URL csvUrl(byte[] csv) throws IOException {
        return URL.of(URI.create("https://example.invalid/videogames"), new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL url) {
                return new URLConnection(url) {
                    @Override
                    public void connect() {
                    }

                    @Override
                    public InputStream getInputStream() {
                        return new ByteArrayInputStream(csv);
                    }
                };
            }
        });
    }

    private static byte[] exampleCsv() throws IOException {
        try (InputStream input = UrlVideogameReaderTest.class.getResourceAsStream("/videogames-example.csv")) {
            if (input == null) {
                throw new IOException("Missing videogames-example.csv");
            }
            return input.readAllBytes();
        }
    }
}
