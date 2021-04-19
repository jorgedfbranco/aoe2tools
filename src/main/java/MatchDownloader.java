import domain.model.Player;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ui.controls.FilterBar;
import ui.controls.MatchesTable;
import ui.controls.PlayersTable;

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

        var playerListing = new PlayersTable() {
            @Override
            public void onPlayerContextMenuCreation(Player player, ContextMenu contextMenu) {
                contextMenu.getItems().add(0, new SeparatorMenuItem());
                var showMatchesMenu = new MenuItem("Show matches");
                showMatchesMenu.setOnAction(e -> matchListing.showMatches(getSelectionModel().getSelectedItem()));
                contextMenu.getItems().add(0, showMatchesMenu);
            }
        };

        var showMatchesMenu = new MenuItem("Show matches");
        showMatchesMenu.setOnAction(e -> matchListing.showMatches(playerListing.getSelectionModel().getSelectedItem()));
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
                matchListing.showMatches(playerListing.getSelectionModel().getSelectedItem());
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