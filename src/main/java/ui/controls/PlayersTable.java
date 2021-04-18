package ui.controls;

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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import ui.model.PlayerRow;
import ui.factories.RatingColumnCellFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class PlayersTable extends TableView<PlayerRow> {
    private final ObservableList<PlayerRow> playerListing = FXCollections.observableList(new ArrayList<>());
    private final BooleanProperty currentlySearching = new SimpleBooleanProperty(false);

    public PlayersTable() {
        setItems(playerListing);
        setPlaceholder(new Label());

        var rating1x1Column = new TableColumn<PlayerRow, Integer>("1x1");
        rating1x1Column.setCellValueFactory(new PropertyValueFactory<>("_1x1Rating"));
        rating1x1Column.setCellFactory(new RatingColumnCellFactory<>());
        getColumns().add(rating1x1Column);

        var ratingTGColumn = new TableColumn<PlayerRow, Integer>("TG");
        ratingTGColumn.setCellValueFactory(new PropertyValueFactory<>("tgRating"));
        ratingTGColumn.setCellFactory(new RatingColumnCellFactory<>());
        getColumns().add(ratingTGColumn);

        var ratingUnrankedColumn = new TableColumn<PlayerRow, Integer>("Unranked");
        ratingUnrankedColumn.setCellValueFactory(new PropertyValueFactory<>("unranked"));
        ratingUnrankedColumn.setCellFactory(new RatingColumnCellFactory<>());
        getColumns().add(ratingUnrankedColumn);

        var nickColumn = new TableColumn<PlayerRow, String>("Nick");
        nickColumn.setPrefWidth(200);
        nickColumn.setCellValueFactory(new PropertyValueFactory<>("nick"));
        nickColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<PlayerRow, String> call(TableColumn<PlayerRow, String> playerRowStringTableColumn) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String v, boolean empty) {
                        super.updateItem(v, empty);
                        setText("");
                        setGraphic(null);
                        var player = getTableRow().getItem();
                        if (player != null)
                            setGraphic(new PlayerLabel(player.player, false));
                    }
                };
            }
        });
        getColumns().add(nickColumn);
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
