package software.ulpgc.katas.service;

import software.ulpgc.katas.model.Metadata;
import software.ulpgc.katas.model.Book;
import software.ulpgc.katas.model.BookExport;
import software.ulpgc.katas.parser.CsvParser;
import software.ulpgc.katas.parser.BookParser;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class BookTransformer {
    public BookExport transform(List<String> lines) throws IOException {
        if (lines.isEmpty()) {
            return new BookExport(List.of(), new Metadata(0, 0, 0));
        }

        List<String> header = CsvParser.parse(lines.getFirst())
                .orElseThrow(() -> new IOException("Malformed CSV header."));
        if (!header.equals(List.of("identifier", "title", "creator", "date"))) {
            throw new IOException("Expected CSV columns: identifier,title,creator,date.");
        }

        List<Book> books = lines.stream()
                .skip(1)
                .map(BookParser::parse)
                .flatMap(Optional::stream)
                .toList();
        int total = lines.size() - 1;
        return new BookExport(books,
                new Metadata(total, books.size(), total - books.size()));
    }
}
