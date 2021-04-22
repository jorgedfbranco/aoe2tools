package ui.controls;

import domain.model.Lobby;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import ui.controls.playerstable.PlayerLabel;

public class MatchPane extends GridPane {
    public MatchPane() {

    }

    public void loadMatch(Lobby match) {
        clearMatch();
        add(boldLabel("Title"), 0, 0); // TODO: this should take as many cells as needed
        add(new Label(match.title()), 1, 0,  2, 1);

        add(boldLabel("Player"), 0, 1);
        add(boldLabel("Color"), 1, 1);
        add(boldLabel("Team"), 2, 1);

        int i = 2;
        for (var slot : match.slots()) {
            if (slot.player().isPresent()) {
                var player = slot.player().get();
                add(new PlayerLabel(player, false, false), 0, i);
                add(new Label(slot.color().orElse(0).toString()), 1, i);
                add(new Label(slot.team().orElse(0).toString()), 2, i);
                i++;
            }
        }
    }

    public void clearMatch() {
        getChildren().clear();
    }

    private Label boldLabel(String caption) {
        var label = new Label(caption);
        label.setStyle("-fx-font-weight: bold;");
        return label;
    }
}
