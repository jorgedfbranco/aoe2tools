package ui.controls;

import domain.model.Player;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.SetChangeListener;
import ui.model.RatingColor;
import ui.viewmodel.SteamServiceViewModel;

public class PlayerRatingLabelViewModel {
    public SimpleStringProperty Style = new SimpleStringProperty();
    public SimpleStringProperty Text = new SimpleStringProperty();

    public PlayerRatingLabelViewModel(Player player) {
        SteamServiceViewModel.SteamFriends.addListener((SetChangeListener<Long>) change -> refreshUI(player));
        refreshUI(player);
    }

    private void refreshUI(Player player) {
        var txt = player.name();
        String color = RatingColor.color(player.rating());
        if (player.rating() != 0)
            txt += " (" + player.rating() + ")";
        Text.setValue(txt);

        String friendStyle = "";
        if (SteamServiceViewModel.SteamFriends.contains(player.steamId()))
            friendStyle = "-fx-border-color: black; -fx-border-width: 1px;";
        String baseStyle = "-fx-text-fill: " + color + "; -fx-font-weight: bold; " + friendStyle;
        Style.setValue(baseStyle);
    }
}
