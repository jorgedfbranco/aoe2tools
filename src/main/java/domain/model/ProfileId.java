package domain.model;

/**
 * An id uniquely identifying every AOE2 player.
 * Not to be confused with SteamId, an id that only steam users have.
 */
public record ProfileId(long id) { }
