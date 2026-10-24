package software.ulpgc.katas.io;

import software.ulpgc.katas.model.Videogame;
import software.ulpgc.katas.model.VideogameExport;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class UrlVideogameReader implements VideogameReader{
    private final URL url;

    public UrlVideogameReader(URL url) {
        this.url = url;
    }

    @Override
    public VideogameExport readAll() throws IOException {
        try (InputStream inputStream = url.openStream()) {
            return VideogameReader.loadFrom(inputStream);
        }
    }

}