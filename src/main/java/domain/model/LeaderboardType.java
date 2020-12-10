package domain.model;

public enum LeaderboardType {
    Unranked(0),
    _1x1_RM(3),
    _1x1DM(1),
    TG_RM(4),
    TG_DM(2);

    private final int code;

    LeaderboardType(int code) {
        this.code = code;
    }

    public int code() { return code; }
}
