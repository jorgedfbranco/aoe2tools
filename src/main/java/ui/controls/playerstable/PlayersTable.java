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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

public class PlayersTable extends TableView<PlayerRow> {
    private final ObservableList<PlayerRow> playerListing = FXCollections.observableList(new ArrayList<>());
    private final BooleanProperty currentlySearching = new SimpleBooleanProperty(false);
    private final ExecutorService threadpool;

    public void onPlayerContextMenuCreation(Player player, ContextMenu contextMenu) { }

    public PlayersTable(ExecutorService threadpool) {
        this.threadpool = threadpool;
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
        progress.setMaxWidth(24);
        vbox.setAlignment(Pos.CENTER);
        setPlaceholder(vbox);
        currentlySearching.setValue(true);

        int maxCount = 20;

        var unranked = new AtomicReference<List<Player>>();
        var ranked1x1 = new AtomicReference<List<Player>>();
        var rankedTG = new AtomicReference<List<Player>>();

        var unrankedFuture = CompletableFuture.runAsync(() -> unranked.set(Aoe2DotNetService.findPlayers(filter, maxCount, LeaderboardType.Unranked)), threadpool);
        var ranked1x1Future = CompletableFuture.runAsync(() -> ranked1x1.set(Aoe2DotNetService.findPlayers(filter, maxCount, LeaderboardType._1x1_RM)), threadpool);
        var rankedTGFuture = CompletableFuture.runAsync(() -> rankedTG.set(Aoe2DotNetService.findPlayers(filter, maxCount, LeaderboardType.TG_RM)), threadpool);

        CompletableFuture.allOf(unrankedFuture, ranked1x1Future, rankedTGFuture).whenComplete((a, b) -> {
            var playerIds = new HashMap<ProfileId, Player>();
            var ratingUnranked = new HashMap<ProfileId, Integer>();
            var rating1x1RM = new HashMap<ProfileId, Integer>();
            var ratingTgRM = new HashMap<ProfileId, Integer>();

            for (Player player : unranked.get()) {
                playerIds.put(player.id(), player);
                ratingUnranked.put(player.id(), player.rating());
            }

            for (Player player : ranked1x1.get()) {
                playerIds.put(player.id(), player);
                rating1x1RM.put(player.id(), player.rating());
            }

            for (Player player : rankedTG.get()) {
                playerIds.put(player.id(), player);
                ratingTgRM.put(player.id(), player.rating());
            }

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
        });
    }
}
