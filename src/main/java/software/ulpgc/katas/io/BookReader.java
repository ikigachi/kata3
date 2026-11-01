package software.ulpgc.katas.io;

import software.ulpgc.katas.model.BookExport;
import software.ulpgc.katas.service.BookTransformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public interface BookReader {
    BookExport read(int count) throws IOException;

    static BookExport loadFrom(InputStream input) throws IOException {
        return loadFrom(new InputStreamReader(input, StandardCharsets.UTF_8));
    }

    static BookExport loadFrom(Reader reader) throws IOException {
        return new BookTransformer().transform(reader.readAllLines());
    }
}
