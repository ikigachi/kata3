package software.ulpgc.katas.io;

import software.ulpgc.katas.model.VideogameExport;
import software.ulpgc.katas.service.VideogameTransformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public interface VideogameReader {
    VideogameExport read(int count) throws IOException;

    static VideogameExport loadFrom(InputStream input) throws IOException {
        return loadFrom(new InputStreamReader(input, StandardCharsets.UTF_8));
    }

    static VideogameExport loadFrom(Reader reader) throws IOException {
        return new VideogameTransformer().transform(reader.readAllLines());
    }
}
