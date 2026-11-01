package software.ulpgc.katas.io;

import software.ulpgc.katas.model.MovieExport;
import software.ulpgc.katas.service.MovieTransformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public interface MovieReader {
    MovieExport read(int count) throws IOException;

    static MovieExport loadFrom(InputStream input) throws IOException {
        return loadFrom(new InputStreamReader(input, StandardCharsets.UTF_8));
    }

    static MovieExport loadFrom(Reader reader) throws IOException {
        return new MovieTransformer().transform(reader.readAllLines());
    }
}
