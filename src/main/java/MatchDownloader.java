import domain.Aoe2DotNetService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ui.viewmodel.LobbyViewModel;
import ui.controls.PlayersTable;
import ui.controls.FilterBar;
import ui.controls.MatchesTable;

import java.util.Optional;

public class MatchDownloader extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/outpost.png")));

        var vbox = new VBox();
        vbox.setPadding(new Insets(8, 8, 8, 8));
        vbox.setSpacing(8);

        var matchListing = new MatchesTable();
        VBox.setVgrow(matchListing, Priority.ALWAYS);

        var playerListing = new PlayersTable();
        playerListing.setPrefHeight(200);
        var filterBar = new FilterBar("Search player:") {
            @Override
            protected void onFilter(String filter) {
                matchListing.matches.clear();
                matchListing.setPlaceholder(new Label());
                playerListing.search(filter);
            }
        };
        filterBar.searchButtonDisabledProperty().bind(playerListing.currentlySearchingProperty());

        vbox.getChildren().add(filterBar);

        vbox.getChildren().add(new Label("Player Listing:"));
        vbox.getChildren().add(playerListing);

        playerListing.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                // TODO: we need to move all this logic to inside matchListing..
                var player = playerListing.getSelectionModel().getSelectedItem();
                if (player != null) {
                    // TODO: use threadpool
                    matchListing.matches.clear();
                    var progress = new ProgressIndicator();
                    progress.setMaxWidth(24);
                    matchListing.setPlaceholder(progress);
                    // TODO: we need to unite this placeholder progressbar logic with the other one of playerListing..
                    new Thread(() -> {
                        var lobbies = Aoe2DotNetService.getMatches(player.player.steamId());
                        Platform.runLater(() -> {
                            lobbies.forEach(lobby -> matchListing.matches.add(new LobbyViewModel(lobby, Optional.empty())));
                            if (matchListing.matches.isEmpty())
                                matchListing.setPlaceholder(new Label("No matches found for player."));
                        });
                    }).start();
                }
            }
        });

        vbox.getChildren().add(new Label("Matches Listing:"));
        vbox.getChildren().add(matchListing);

        primaryStage.setTitle("AOE2DE Match Browser");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(1000);
        primaryStage.setScene(new Scene(vbox, 0, 0));
        primaryStage.show();
    }
}