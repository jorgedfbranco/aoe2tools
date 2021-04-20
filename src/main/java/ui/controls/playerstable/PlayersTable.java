package ui.controls.playerstable;

import domain.Aoe2DotNetService;
import domain.model.LeaderboardType;
import domain.model.Player;
import domain.model.ProfileId;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import ui.model.PlayerRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class PlayersTable extends TableView<PlayerRow> {
    private final ObservableList<PlayerRow> playerListing = FXCollections.observableList(new ArrayList<>());
    private final BooleanProperty currentlySearching = new SimpleBooleanProperty(false);

    public void onPlayerContextMenuCreation(Player player, ContextMenu contextMenu) { }

    public PlayersTable() {
        setItems(playerListing);
        setPlaceholder(new Label());
        setContextMenu(new ContextMenu());
    }

    public BooleanProperty currentlySearchingProperty() { return currentlySearching; }

    public void search(String filter) {
        playerListing.clear();

        var vbox = new VBox();
        vbox.setSpacing(8);
        var progress = new ProgressIndicator();
        var label = new Label();
        label.setStyle("-fx-font-style: italic;");
        vbox.getChildren().add(label);
        vbox.getChildren().add(progress);
        progress.setProgress(0);
        progress.setPrefWidth(24);
        vbox.setAlignment(Pos.CENTER);
        setPlaceholder(vbox);
        currentlySearching.setValue(true);

        // TODO: use threadpool instead
        new Thread(() -> {
            var playerIds = new HashMap<ProfileId, Player>();
            var ratingUnranked = new HashMap<ProfileId, Integer>();
            var rating1x1RM = new HashMap<ProfileId, Integer>();
            var ratingTgRM = new HashMap<ProfileId, Integer>();
            int maxCount = 20;

            Platform.runLater(() -> label.setText("Searching players.. Unranked"));
            for (Player player : Aoe2DotNetService.findPlayers(filter, maxCount, LeaderboardType.Unranked)) {
                playerIds.put(player.id(), player);
                ratingUnranked.put(player.id(), player.rating());
            }
            Platform.runLater(() -> progress.setProgress(0.33));

            Platform.runLater(() -> label.setText("Searching players.. 1x1 RM"));
            for (Player player : Aoe2DotNetService.findPlayers(filter, maxCount, LeaderboardType._1x1_RM)) {
                playerIds.put(player.id(), player);
                rating1x1RM.put(player.id(), player.rating());
            }
            Platform.runLater(() -> progress.setProgress(0.66));

            Platform.runLater(() -> label.setText("Searching players.. TG RM"));
            for (Player player : Aoe2DotNetService.findPlayers(filter, maxCount, LeaderboardType.TG_RM)) {
                playerIds.put(player.id(), player);
                ratingTgRM.put(player.id(), player.rating());
            }
            Platform.runLater(() -> progress.setProgress(1));

            Platform.runLater(() -> {
                for (ProfileId id : playerIds.keySet()) {
                    playerListing.add(new PlayerRow(
                        playerIds.get(id),
                        Optional.ofNullable(ratingUnranked.get(id)),
                        Optional.ofNullable(rating1x1RM.get(id)),
                        Optional.ofNullable(ratingTgRM.get(id)))
                    );
                }
                if (playerListing.isEmpty()) {
                    setPlaceholder(new Label("No players found."));
                }
                currentlySearching.setValue(false);
            });
        }).start();
    }
}
