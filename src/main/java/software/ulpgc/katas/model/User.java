package software.ulpgc.katas.model;

import java.time.Instant;

public record User(String name, Instant birthday, byte[] picture) {
}
