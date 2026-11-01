package software.ulpgc.katas.model;

public record Movie(String identifier, String title, String creator, int year) {
    public Movie {
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("Identifier is required.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is required.");
        }
        if (creator == null || creator.isBlank()) {
            throw new IllegalArgumentException("Creator is required.");
        }
        if (year < 1) {
            throw new IllegalArgumentException("Year must be positive.");
        }
    }
}
