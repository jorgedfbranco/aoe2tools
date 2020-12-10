package domain.model;

import java.util.Optional;

public record Slot(
    SlotType type,
    Optional<Player> player,
    Optional<Boolean> won,
    Optional<Integer> team,
    Optional<String> civ,
    Optional<Integer> color) { }
