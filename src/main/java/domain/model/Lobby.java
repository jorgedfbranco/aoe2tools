package domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public record Lobby(
    long id,
    String title,
    int numPlayers,
    int slotCount /* TODO: could this be inferred from `slots`? */,
    boolean passwordProtected,
    String server,
    int averageRating,
    String mapSize,
    int pop,
    boolean ranked,
    String gameType,
    String speed,
    String visibility,
    String mapType,
    Optional<LocalDateTime> created,
    List<Slot> slots) {
}