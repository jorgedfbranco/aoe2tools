package domain.model;

import java.util.Optional;

public record PlayerRatings(Optional<Rating> _1x1, Optional<Rating> unranked, Optional<Rating> teamGame) { }
