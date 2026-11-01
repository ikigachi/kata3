package software.ulpgc.katas.model;

public record Book(String identifier, String title, String author, int year) {
    public Book {
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("Identifier is required.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is required.");
        }
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Author is required.");
        }
        if (year < 1) {
            throw new IllegalArgumentException("Year must be positive.");
        }
    }
}
