package ui.model;

import domain.RatingsService;
import domain.model.RatingType;
import domain.model.Slot;
import domain.model.SteamId;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SlotViewModel {
    private final Slot slot;

    public final StringProperty _1x1Rating = new SimpleStringProperty("-");
    public final StringProperty tgRating = new SimpleStringProperty("-");
    public final StringProperty unrankedRating = new SimpleStringProperty("-");

    public SlotViewModel(Slot slot) {
        this.slot = slot;
    }

    public Slot getSlot() { return slot; }

    public void calculateRatings() {
        if (slot.player().isPresent()) {
            var player = slot.player().get();
            updateRatings(player.steamId(), RatingType._1x1, _1x1Rating);
            updateRatings(player.steamId(), RatingType.TeamGame, tgRating);
            updateRatings(player.steamId(), RatingType.Unranked, unrankedRating);
        }
    }

    private void updateRatings(SteamId steamId, RatingType ratingType, StringProperty ratingProperty) {
        RatingsService.getRatings(steamId, ratingType).whenComplete((v, e) -> {
            Platform.runLater(() -> {
                if (v.isPresent()) {
                    ratingProperty.set(String.valueOf(v.get().rating()));
                } else {
                    ratingProperty.set("-");
                }
            });
        });
    }
}