package ui.model;

import domain.Aoe2Service;
import domain.model.Lobby;
import domain.model.Player;
import javafx.beans.property.*;

import java.util.Optional;

public class PlayerRow {
    // TODO: most or all of these properties can be turned into regular fields, it seems
    private final StringProperty nick = new SimpleStringProperty();
    private final IntegerProperty unrankedRating = new SimpleIntegerProperty();
    private final IntegerProperty _1x1Rating = new SimpleIntegerProperty();
    private final IntegerProperty tgRating = new SimpleIntegerProperty();
    public final Player player;

    private final StringProperty lastGameText = new SimpleStringProperty("Unknown");
    private final BooleanProperty canSpectate = new SimpleBooleanProperty(false);
    private final StringProperty dummy = new SimpleStringProperty("");
    private Optional<Lobby> lastMatch = Optional.empty();

    public PlayerRow(Player player, Optional<Integer> unrankedRating, Optional<Integer> _1x1RMRating, Optional<Integer> tgRMRating) {
        this.nick.setValue(player.name());
        this.player = player;

        unrankedRating.ifPresent(this.unrankedRating::set);
        _1x1RMRating.ifPresent(this._1x1Rating::set);
        tgRMRating.ifPresent(this.tgRating::set);
    }

    public StringProperty dummyProperty() { return nick; }
    public StringProperty nickProperty() { return nick; }
    public IntegerProperty unrankedProperty() { return unrankedRating; }
    public IntegerProperty _1x1RatingProperty() { return _1x1Rating; }
    public IntegerProperty tgRatingProperty() { return tgRating; }
    public StringProperty lastGameTextProperty() { return lastGameText; }
    public BooleanProperty canSpectateProperty() { return canSpectate; }

    public void spectate() {
        lastMatch.ifPresent(k -> Aoe2Service.spectateGame(k.id()));
    }
}