package software.ulpgc.katas.io;

import software.ulpgc.katas.model.BookExport;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ArchiveBookReader implements BookReader {
    private final CsvSource source;

    public ArchiveBookReader(CsvSource source) {
        this.source = source;
    }

    @Override
    public BookExport read(int count) throws IOException {
        if (count < 1) {
            throw new IllegalArgumentException("Count must be positive.");
        }

        String query = URLEncoder.encode(
                "collection:internetarchivebooks AND creator:* AND date:*",
                StandardCharsets.UTF_8);
        URI uri = URI.create("https://archive.org/advancedsearch.php?q=" + query
                + "&fl%5B%5D=identifier&fl%5B%5D=title&fl%5B%5D=creator&fl%5B%5D=date"
                + "&rows=" + count + "&page=1&output=csv");

        try (InputStream input = source.open(uri)) {
            return BookReader.loadFrom(input);
        }
    }
}
