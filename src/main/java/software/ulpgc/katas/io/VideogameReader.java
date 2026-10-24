package software.ulpgc.katas.io;

import software.ulpgc.katas.model.VideogameExport;
import software.ulpgc.katas.service.VideogameTransformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public interface VideogameReader {
    VideogameExport readAll() throws IOException;

    static VideogameExport loadFrom(InputStream inputStream) throws IOException {
        return loadFrom(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    static VideogameExport loadFrom(Reader reader) throws IOException {
        return loadFrom(reader.readAllLines());
    }

    static VideogameExport loadFrom(List<String> lines) {
        return new VideogameTransformer().transform(lines);
    }
}