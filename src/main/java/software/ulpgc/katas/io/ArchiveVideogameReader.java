package software.ulpgc.katas.io;

import software.ulpgc.katas.model.VideogameExport;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ArchiveVideogameReader implements VideogameReader {
    private final CsvSource source;

    public ArchiveVideogameReader(CsvSource source) {
        this.source = source;
    }

    @Override
    public VideogameExport read(int count) throws IOException {
        if (count < 1) {
            throw new IllegalArgumentException("Count must be positive.");
        }

        String query = URLEncoder.encode(
                "collection:softwarelibrary_msdos_games AND creator:* AND date:*",
                StandardCharsets.UTF_8);
        URI uri = URI.create("https://archive.org/advancedsearch.php?q=" + query
                + "&fl%5B%5D=identifier&fl%5B%5D=title&fl%5B%5D=creator&fl%5B%5D=date"
                + "&rows=" + count + "&page=1&output=csv");

        try (InputStream input = source.open(uri)) {
            return VideogameReader.loadFrom(input);
        }
    }
}
