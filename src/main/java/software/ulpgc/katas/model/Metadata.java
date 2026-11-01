package software.ulpgc.katas.model;

public record Metadata(int totalRecords, int validRecords, int discardedRecords) {
    public Metadata {
        if (totalRecords < 0 || validRecords < 0 || discardedRecords < 0
                || (long) validRecords + discardedRecords != totalRecords) {
            throw new IllegalArgumentException("Record counts must be non-negative and add up to the total.");
        }
    }
}
