package software.ulpgc.katas.model;

import java.util.List;

public record VideogameExport(List<Videogame> videogames, Metadata metadata) {
    public VideogameExport {
        videogames = List.copyOf(videogames);
    }
}
