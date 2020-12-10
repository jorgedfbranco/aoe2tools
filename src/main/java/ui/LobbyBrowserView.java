package ui;

import domain.AppSettingsViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import ui.controls.MatchesTable;
import ui.viewmodel.LobbyBrowserViewModel;
import ui.viewmodel.SteamServiceViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class LobbyBrowserView implements Initializable {
    private final MatchesTable matchesTable = new MatchesTable();
    @FXML private VBox lobbyInfoPanel;
    @FXML private MenuItem refreshLobbyListingMenu;
    @FXML private MenuItem selectFilteringMenu;
    @FXML private TextField filterTextField;
    @FXML private ProgressIndicator statusBarRightProgressIndicator;
    @FXML private CheckMenuItem showAutomatchMenu;
    @FXML private CheckMenuItem showCountryFlagsMenu;
    @FXML private CheckMenuItem showFavouriteMapsOnlyMenu;
    @FXML private CheckMenuItem colorPlayersRatingMenu;
    @FXML private CheckMenuItem showRatingsMenu;

//    @FXML private TableColumn<LobbyViewModel, String> mapColumn;
//    @FXML private TableColumn<LobbyViewModel, Integer> ratingColumn;
//    @FXML private TableColumn<LobbyViewModel, List<Slot>> playersColumn;
//    @FXML private TableColumn<LobbyViewModel, Lobby> slotsColumn;
//    @FXML private TableColumn<LobbyViewModel, Lobby> titleColumn;

//    @FXML private MenuItem joinLobbyMenu;

    private final LobbyBrowserViewModel viewModel = new LobbyBrowserViewModel();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        mapColumn.setCellFactory(new MapColumnCellFactory(statusBar));
//
//        ratingColumn.setCellFactory(new RatingColumnCellFactory());
//        playersColumn.setCellFactory(new PlayersColumnCellFactory(statusBar));
//        slotsColumn.setCellFactory(new PlayerSlotsColumnCellFactory());
//        titleColumn.setCellFactory(new TitleColumnCellFactory());

//        matchesTable.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener() {
//            @Override
//            public void onChanged(Change change) {
//                var items = matchesTable.getSelectionModel().getSelectedItems();
//                if (items.size() != 0) {
//                    var lobby = items.get(0);
//                    viewModel.setSelectedLobby(lobby);
//                    lobbyInfoPanel.getChildren().clear();
//
//                    var grid = new GridPane();
//                    grid.setStyle("-fx-padding: 8;");
//                    grid.setVgap(4);
//                    grid.setHgap(4);
//
//                    int j = 0;
//                    var titleLabel = new Label("Title:");
//                    titleLabel.setStyle("-fx-font-weight: bold;");
//                    grid.add(titleLabel, 0, 0);
//                    grid.add(new Label(lobby.getTitle()), 1, j++);
//
//                    var mapSize = new Label("Map Size:");
//                    mapSize.setStyle("-fx-font-weight: bold;");
//                    grid.add(mapSize, 0, 1);
//                    grid.add(new Label(lobby.getMapSize()), 1, j++);
//
//                    var created = new Label("Created:");
//                    created.setStyle("-fx-font-weight: bold;");
//                    grid.add(created, 0, 2);
//                    grid.add(new Label(lobby.getCreated()), 1, j++);
//
//                    var players = new Label("Players:");
//                    players.setStyle("-fx-font-weight: bold;");
//                    grid.add(players, 0, j);
//
//                    for (var player : lobby.getPlayers()) {
//                        if (player.getSlot().player().isEmpty()) {
//                            grid.add(new Label("-"), 1, j++);
//                        } else {
//                            grid.add(new Label(player.getSlot().player().get().name()), 1, j);
//
//                            var _1x1Label = new Label();
//                            _1x1Label.textProperty().bindBidirectional(player._1x1Rating);
//                            grid.add(_1x1Label, 2, j);
//
//                            var tgLabel = new Label(String.valueOf(player.tgRating.get())) ;
//                            tgLabel.textProperty().bindBidirectional(player.tgRating);
//                            grid.add(tgLabel, 3, j);
//
//                            var unrankedLabel = new Label(String.valueOf(player.unrankedRating.get())) ;
//                            unrankedLabel.textProperty().bindBidirectional(player.unrankedRating);
//                            grid.add(unrankedLabel, 4, j);
//
//                            player.calculateRatings();
//                            j++;
//                        }
//                    }
//
//                    lobbyInfoPanel.getChildren().add(grid);
//                }
//            }
//        });

//        matchesTable.getSortOrder().setAll(ratingColumn);

        var placeholderVBox = new VBox();
        placeholderVBox.setAlignment(Pos.CENTER);
        var progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxWidth(30);
        placeholderVBox.getChildren().add(progressIndicator);
        placeholderVBox.getChildren().add(new Label("Starting application..."));
        matchesTable.setPlaceholder(placeholderVBox);

        showFavouriteMapsOnlyMenu.selectedProperty().bindBidirectional(AppSettingsViewModel.ShowFavouritesOnly);
        showAutomatchMenu.selectedProperty().bindBidirectional(AppSettingsViewModel.ShowAutomatches);
        colorPlayersRatingMenu.selectedProperty().bindBidirectional(AppSettingsViewModel.ColorPlayersRating);
        showRatingsMenu.selectedProperty().bindBidirectional(AppSettingsViewModel.ShowRatings);

        // TODO: this should be going through the viewmodel..
        showCountryFlagsMenu.selectedProperty().bindBidirectional(AppSettingsViewModel.ShowCountryFlags);

//        joinLobbyMenu.setOnAction(k -> {
//            var lobby = matchesTable.getSelectionModel().getSelectedItems().get(0);
//            viewModel.joinLobby(lobby.getId());
//        });

        matchesTable.setItems(viewModel.sortedLobbies);
        viewModel.sortedLobbies.comparatorProperty().bind(matchesTable.comparatorProperty());

        refreshLobbyListingMenu.setOnAction(k -> viewModel.refreshLobbies());
        refreshLobbyListingMenu.setAccelerator(KeyCombination.keyCombination("CTRL+R"));

        selectFilteringMenu.setOnAction(k -> filterTextField.requestFocus());
        selectFilteringMenu.setAccelerator(KeyCombination.keyCombination("CTRL+F"));

        filterTextField.textProperty().bindBidirectional(AppSettingsViewModel.FilterString);

        SteamServiceViewModel.refreshFriends(76561199072704895L);
    }
}