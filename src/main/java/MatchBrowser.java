import domain.Aoe2Service;
import domain.model.ProfileId;
import infra.HttpService;
import infra.WindowsService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.controls.FilterBar;
import ui.controls.MatchPane;
import ui.controls.matchestable.*;
import ui.controls.playerstable.NickColumn;
import ui.controls.playerstable.PlayerLabel;
import ui.controls.playerstable.PlayersTable;
import ui.controls.playerstable.RatingColumn;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MatchBrowser extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/outpost.png")));

        var vbox = new VBox();
        vbox.setPadding(new Insets(8, 8, 8, 8));
        vbox.setSpacing(8);

        var threadpool = Executors.newCachedThreadPool();

        var matchListing = new MatchesTable(threadpool);
        VBox.setVgrow(matchListing, Priority.ALWAYS);

        var playerListing = new PlayersTable(threadpool);
        var matchPane = new MatchPane();
        var viewModel = new MatchDownloaderController(matchListing, playerListing, matchPane, threadpool);

        playerListing.getColumns().add(RatingColumn._1x1RatingColumn);
        playerListing.getColumns().add(RatingColumn.tgRatingColumn);
        playerListing.getColumns().add(RatingColumn.unrankedRatingColumn);
        playerListing.getColumns().add(new NickColumn() {
            @Override
            public void onPlayerLabelCreated(PlayerLabel label) {
                label.getContextMenu().getItems().add(0, new SeparatorMenuItem());
                var showMatchesMenu = new MenuItem("Show matches");
                showMatchesMenu.setOnAction(e -> viewModel.onShowMatchesAction());
                label.getContextMenu().getItems().add(0, showMatchesMenu);
            }
        });
        var showMatchesMenu = new MenuItem("Show matches");
        showMatchesMenu.setOnAction(e -> viewModel.onShowMatchesAction());
        playerListing.getContextMenu().getItems().add(showMatchesMenu);

        matchListing.getColumns().add(new MapColumn());
        matchListing.getColumns().add(new TitleColumn());

        var spectateMenu = new MenuItem("Spectate Match");
        spectateMenu.setOnAction(e -> viewModel.spectateGame());
        matchListing.getContextMenu().getItems().add(spectateMenu);

        var spectateLinkMenu = new MenuItem("Copy Spectate Link");
        spectateLinkMenu.setOnAction(e -> viewModel.copySpectateLink());
        matchListing.getContextMenu().getItems().add(spectateLinkMenu);

        var downloadMenu = new MenuItem("Download Match");
        downloadMenu.setOnAction(e -> viewModel.downloadGame());
        matchListing.getContextMenu().getItems().add(downloadMenu);

        matchListing.getColumns().add(new PlayersColumn() {
            @Override
            public void onPlayerLabelCreated(PlayerLabel label) {
                var spectateMenu = new MenuItem("Spectate Match");
                label.getContextMenu().getItems().add(0, spectateMenu);
                spectateMenu.setOnAction(e -> viewModel.spectateGame());

                var copySpectateLinkMenu = new MenuItem("Copy Spectate Link");
                label.getContextMenu().getItems().add(1, copySpectateLinkMenu);
                copySpectateLinkMenu.setOnAction(e -> viewModel.copySpectateLink());

                var copyDownloadLinkMenu = new MenuItem("Copy Download Link");
                label.getContextMenu().getItems().add(2, copyDownloadLinkMenu);
                copyDownloadLinkMenu.setOnAction(e -> viewModel.copyDownloadLink(label.playerProperty().get().id()));

                var downloadMatchMenu = new MenuItem("Download Match");
                label.getContextMenu().getItems().add(3, downloadMatchMenu);
                downloadMatchMenu.setOnAction(e -> viewModel.downloadGame(label.playerProperty().get().id()));

                label.getContextMenu().getItems().add(4, new SeparatorMenuItem());

                if (!label.playerProperty().get().id().equals(matchListing.playerIdProperty().get())) {
                    label.getContextMenu().getItems().add(new SeparatorMenuItem());
                    var showMatchesMenu = new MenuItem("Show Matches");
                    showMatchesMenu.setOnAction(e -> viewModel.onShowPlayerMatchesAction(label.playerProperty().get().id()));
                    label.getContextMenu().getItems().add(showMatchesMenu);
                }
            }
        });

        matchListing.getColumns().add(new CreatedColumn());

        playerListing.setPrefHeight(200);
        var filterBar = new FilterBar("Search player:") {
            @Override
            protected void onFilter(String filter) {
                matchListing.clear();
                matchPane.clearMatch();
                matchListing.setPlaceholder(new Label());
                playerListing.search(filter);
            }
        };
        playerListing.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                var profileId = playerListing.getSelectionModel().getSelectedItem().player.id();
                matchPane.clearMatch();
                matchListing.showMatches(profileId);
            }
        });
        filterBar.searchButtonDisabledProperty().bind(playerListing.currentlySearchingProperty());

        vbox.getChildren().add(filterBar);

        vbox.getChildren().add(new Label("Player Listing:"));
        vbox.getChildren().add(playerListing);

        vbox.getChildren().add(new Label("Matches Listing:"));
        vbox.getChildren().add(matchListing);

        matchListing.getSelectionModel().selectedItemProperty().addListener((observableValue, m, t1) -> {
            var match = matchListing.getSelectionModel().getSelectedItem();
            if (match != null)
                matchPane.loadMatch(match.getLobby());
        });
        vbox.getChildren().add(matchPane);

        primaryStage.setTitle("AOE2DE Match Browser");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(1000);
        primaryStage.setScene(new Scene(vbox, 0, 0));
        primaryStage.show();
    }
}

class MatchDownloaderController {
    private final PlayersTable playersListing;
    private final MatchesTable matchesListing;
    private final MatchPane matchPane;
    private final ExecutorService threadpool;

    public MatchDownloaderController(MatchesTable matchListing, PlayersTable playerListing, MatchPane matchPane, ExecutorService threadpool) {
        this.matchesListing = matchListing;
        this.playersListing = playerListing;
        this.matchPane = matchPane;
        this.threadpool = threadpool;
    }

    public void onShowMatchesAction() {
        var selectedItem = playersListing.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            var profileId = selectedItem.player.id();
            matchesListing.showMatches(profileId);
        }
    }

    public void spectateGame() {
        var selectedItem = matchesListing.getSelectionModel().getSelectedItem();
        if (selectedItem != null)
            Aoe2Service.spectateGame(selectedItem.getId());
    }

    public void copySpectateLink() {
        var selectedItem = matchesListing.getSelectionModel().getSelectedItem();
        if (selectedItem != null)
            Aoe2Service.copySpectateLink(selectedItem.getId());
    }

    public void downloadGame(ProfileId profileId) {
        var selectedItem = matchesListing.getSelectionModel().getSelectedItem();
        if (selectedItem != null)
            Aoe2Service.downloadGame(selectedItem.getId(), profileId);
    }

    public void copyDownloadLink(ProfileId profileId) {
        var selectedItem = matchesListing.getSelectionModel().getSelectedItem();
        if (selectedItem != null)
            WindowsService.setClipboard(Aoe2Service.downloadLink(selectedItem.getId(), profileId));
    }

    public void downloadGame() {
        var selectedItem = matchesListing.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            var matchId = selectedItem.getId();

            var stage = new Stage();
            stage.setTitle("Searching Recorded Games..");
            stage.initModality(Modality.APPLICATION_MODAL);

            var vbox = new VBox();
            var progressbar = new ProgressBar();
            var label = new Label();
            vbox.getChildren().add(progressbar);
            vbox.getChildren().add(label);

            var scene = new Scene(vbox);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.setWidth(300);
            stage.setHeight(200);

            var playerCount = selectedItem.getPlayers().size();
            var i = new AtomicInteger(0);
            progressbar.setProgress(0);

            threadpool.submit(() -> {
                for (var slot : selectedItem.getPlayers()) {
                    var player = slot.getSlot().player();
                    if (player.isPresent()) {
                        Platform.runLater(() -> label.setText("Checking " + player.get().name() + "'s POV..."));
                        var profileId = player.get().id();
                        var downloadLink = Aoe2Service.downloadLink(matchId, profileId);
                        if (HttpService.checkUrlExists(downloadLink)) {
                            Aoe2Service.downloadGame(matchId, profileId);
                            Platform.runLater(stage::close);
                            return;
                        }
                        Platform.runLater(() -> {
                            progressbar.setProgress(1.0 * i.incrementAndGet() / playerCount);
                        });
                    }
                }
                Platform.runLater(() -> {
                    var alert = new Alert(Alert.AlertType.WARNING, "Could not find any valid download link.\r\nEither the match's still ongoing or the match's too old?");
                    alert.initStyle(StageStyle.UTILITY);
                    alert.setTitle("No match found.");
                    alert.setHeaderText(null);
                    alert.showAndWait();
                    Platform.runLater(stage::close);
                });
            });
            stage.showAndWait();
        }
    }

    public void onShowPlayerMatchesAction(ProfileId id) {
        matchPane.clearMatch();
        matchesListing.showMatches(id);
    }
}