package ui.controls;

import domain.model.Lobby;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ui.AppResources;
import ui.controls.playerstable.PlayerLabel;

public class MatchPane extends VBox {
    public void loadMatch(Lobby match) {
        clearMatch();

        var header = new GridPane();
        header.setHgap(4);
        header.setVgap(4);

        var players = new GridPane();
        players.setHgap(4);
        players.setVgap(4);

        header.add(boldLabel("Title"), 1, 0);
        header.add(new Label(match.title()), 2, 0,  2, 1);

        header.add(boldLabel("Map"), 1, 1);
        header.add(new Label(match.mapType()), 2, 1);

        header.add(boldLabel("Id"), 1, 2);
        header.add(new Label(String.valueOf(match.id().id())), 2, 2);

        players.add(boldLabel("Player"), 1, 3);
        players.add(boldLabel("Color"), 2, 3);
        players.add(boldLabel("Civ"), 3, 3);
        players.add(boldLabel("Team"), 4, 3);
        players.add(boldLabel("1x1"), 5, 3);

        int i = 4;
        for (var slot : match.slots()) {
            if (slot.player().isPresent()) {
                var player = slot.player().get();
                if (slot.won().orElse(false)) {
                    var imgView = new ImageView(AppResources.WonIcon);
                    imgView.setFitHeight(16);
                    imgView.setFitWidth(16);
                    players.add(imgView, 0, i);
                }
                players.add(new PlayerLabel(player, true, true), 1, i);
                players.add(new Label(slot.color().orElse(0).toString()), 2, i);
                players.add(new Label(slot.civ().orElse("")), 3, i);
                players.add(new Label(slot.team().orElse(0).toString()), 4, i);
                i++;
            }
        }

        getChildren().add(header);
        getChildren().add(players);
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
