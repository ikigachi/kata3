package software.ulpgc.katas.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;

public class UrlCsvSource implements CsvSource {
    @Override
    public InputStream open(URI uri) throws IOException {
        URLConnection connection = uri.toURL().openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setRequestProperty("Accept", "text/csv");
        connection.setRequestProperty("User-Agent", "kata3");
        return connection.getInputStream();
    }
}
