package domain.model;

public record Player(
    SteamId steamId, // there seem to be players with no steam id. i guess they're xbox ones? Ex: "xI Khaleesi x"
    ProfileId id,
    String name,
    String country,
    int rating,
    int games,
    int wins,
    int drops
) {
}
