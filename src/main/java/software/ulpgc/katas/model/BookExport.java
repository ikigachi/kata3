package software.ulpgc.katas.model;

import java.util.List;

public record BookExport(List<Book> books, Metadata metadata) {
    public BookExport {
        books = List.copyOf(books);
    }
}
