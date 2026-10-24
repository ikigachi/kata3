package software.ulpgc.katas.model;

public record Metadata(
        int totalRecords,
        int validRecords,
        int discardedRecords
) {
}