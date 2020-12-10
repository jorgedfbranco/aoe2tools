package domain.model;

import java.time.LocalDateTime;

public record Rating(int rating, int wins, int losses, int streak, int drops, LocalDateTime time) { }
