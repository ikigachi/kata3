package software.ulpgc.katas.io;

import software.ulpgc.katas.model.VideogameExport;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class UrlVideogameReader implements VideogameReader {
    private final URL url;
    private final Map<String, String> headers;

    public UrlVideogameReader(URL url) {
        this(url, Map.of());
    }

    public UrlVideogameReader(URL url, Map<String, String> headers) {
        this.url = url;
        this.headers = Map.copyOf(headers);
    }

    @Override
    public VideogameExport readAll() throws IOException {
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        headers.forEach(connection::setRequestProperty);

        try (InputStream inputStream = connection.getInputStream()) {
            return VideogameReader.loadFrom(inputStream);
        }
    }
}
