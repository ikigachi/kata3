package software.ulpgc.katas.model;

import java.util.List;

public record MovieExport(List<Movie> movies, Metadata metadata) {
    public MovieExport {
        movies = List.copyOf(movies);
    }
}
