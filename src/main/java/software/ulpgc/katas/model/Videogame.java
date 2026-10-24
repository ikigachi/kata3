package software.ulpgc.katas.model;

public record Videogame(
        String rank,
        String name,
        String platform,
        String yearOfRelease,
        String genre,
        String publisher,
        String naSales,
        String euSales,
        String jpSales,
        String otherSales,
        String globalSales
) {
}
