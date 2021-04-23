package ui.controls;

import domain.model.Lobby;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import ui.AppResources;
import ui.controls.playerstable.PlayerLabel;

public class MatchPane extends GridPane {
    public MatchPane() {
        setHgap(4);
        setVgap(4);
    }

    public void loadMatch(Lobby match) {
        clearMatch();

        add(boldLabel("Title"), 1, 0);
        add(new Label(match.title()), 2, 0,  2, 1);

        add(boldLabel("Map"), 1, 1);
        add(new Label(match.mapType()), 2, 1);

        add(boldLabel("Id"), 1, 2);
        add(new Label(String.valueOf(match.id().id())), 2, 2);

        add(boldLabel("Player"), 1, 3);
        add(boldLabel("Color"), 2, 3);
        add(boldLabel("Civ"), 3, 3);
        add(boldLabel("Team"), 4, 3);

        int i = 4;
        for (var slot : match.slots()) {
            if (slot.player().isPresent()) {
                var player = slot.player().get();
                if (slot.won().orElse(false)) {
                    var imgView = new ImageView(AppResources.WonIcon);
                    imgView.setFitHeight(16);
                    imgView.setFitWidth(16);
                    add(imgView, 0, i);
                }
                add(new PlayerLabel(player, true, true), 1, i);
                add(new Label(slot.color().orElse(0).toString()), 2, i);
                add(new Label(slot.civ().orElse("")), 3, i);
                add(new Label(slot.team().orElse(0).toString()), 4, i);
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
