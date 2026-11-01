package software.ulpgc.katas.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

@FunctionalInterface
public interface CsvSource {
    InputStream open(URI uri) throws IOException;
}
