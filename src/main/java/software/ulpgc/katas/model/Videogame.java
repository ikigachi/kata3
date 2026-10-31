package software.ulpgc.katas.model;

public record Videogame(String identifier, String name, String creator, int year) {
    public Videogame {
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("Identifier is required.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required.");
        }
        if (creator == null || creator.isBlank()) {
            throw new IllegalArgumentException("Creator is required.");
        }
        if (year < 1) {
            throw new IllegalArgumentException("Year must be positive.");
        }
    }
}
