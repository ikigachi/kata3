package software.ulpgc.katas.io;

import software.ulpgc.katas.model.Videogame;

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
    public List<Videogame> readAll() {
        try {
            return loadFrom(url);
        } catch (IOException e) {
            return List.of();
        }
    }

    private List<Videogame> loadFrom(URL url) throws IOException {
        try (InputStream inputStream = url.openStream()) {
            return VideogameReader.loadFrom(inputStream);
        }
    }

}