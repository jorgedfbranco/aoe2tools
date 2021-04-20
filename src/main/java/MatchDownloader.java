import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ui.controls.FilterBar;
import ui.controls.PlayerLabel;
import ui.controls.matchestable.MapColumn;
import ui.controls.matchestable.MatchesTable;
import ui.controls.matchestable.PlayersColumn;
import ui.controls.matchestable.TitleColumn;
import ui.controls.playerstable.NickColumn;
import ui.controls.playerstable.PlayersTable;
import ui.controls.playerstable.RatingColumn;

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
        playerListing.getColumns().add(RatingColumn._1x1RatingColumn);
        playerListing.getColumns().add(RatingColumn.tgRatingColumn);
        playerListing.getColumns().add(RatingColumn.unrankedRatingColumn);
        playerListing.getColumns().add(new NickColumn() {
            @Override
            public void onPlayerLabelCreated(PlayerLabel label) {
                label.getContextMenu().getItems().add(0, new SeparatorMenuItem());
                var showMatchesMenu = new MenuItem("Show matches");
                var selectedItem = playerListing.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    var steamId = selectedItem.player.steamId();
                    showMatchesMenu.setOnAction(e -> matchListing.showMatches(steamId));
                    label.getContextMenu().getItems().add(0, showMatchesMenu);
                }
            }
        });

        matchListing.getColumns().add(new MapColumn());
        matchListing.getColumns().add(new TitleColumn());
        matchListing.getColumns().add(new PlayersColumn() {
            @Override
            public void onPlayerLabelCreated(PlayerLabel label) {
                label.getContextMenu().getItems().add(0, new MenuItem("Spectate Match"));
                label.getContextMenu().getItems().add(1, new MenuItem("Download Match"));
                label.getContextMenu().getItems().add(2, new SeparatorMenuItem());
            }
        });

        var showMatchesMenu = new MenuItem("Show matches");
        showMatchesMenu.setOnAction(e -> {
            var steamId = playerListing.getSelectionModel().getSelectedItem().player.steamId();
            matchListing.showMatches(steamId);
        });
        playerListing.getContextMenu().getItems().add(showMatchesMenu);

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
                var steamId = playerListing.getSelectionModel().getSelectedItem().player.steamId();
                matchListing.showMatches(steamId);
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