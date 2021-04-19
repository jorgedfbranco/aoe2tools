package ui.controls;

import domain.Aoe2Service;
import domain.model.Lobby;
import domain.model.Slot;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ui.viewmodel.LobbyViewModel;
import ui.factories.PlayersColumnCellFactory;
import ui.factories.TitleColumnCellFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A TableView displaying one match (or lobby) per row.
 */
public class MatchesTable extends TableView<LobbyViewModel> {
    // TODO: make this private
    public final ObservableList<LobbyViewModel> matches = FXCollections.observableList(new ArrayList<>());

    public MatchesTable() {
        setPlaceholder(new Label());
        setItems(matches);

        var mapColumn = new TableColumn<LobbyViewModel, String>("Map");
        mapColumn.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
        mapColumn.setCellValueFactory(new PropertyValueFactory<>("mapType"));
        mapColumn.setPrefWidth(125);
        getColumns().add(mapColumn);

        var titleColumn = new TableColumn<LobbyViewModel, Lobby>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("lobby"));
        titleColumn.setCellFactory(new TitleColumnCellFactory());
        titleColumn.setPrefWidth(350);
        titleColumn.setStyle("-fx-font-weight: bold;");
        getColumns().add(titleColumn);

        var playersColumn = new TableColumn<LobbyViewModel, List<Slot>>("Players");
        playersColumn.setCellValueFactory(new PropertyValueFactory<>("players"));
        playersColumn.setCellFactory(new PlayersColumnCellFactory());
        playersColumn.setPrefWidth(1000);
        playersColumn.setStyle("-fx-font-weight: bold;");
        getColumns().add(playersColumn);

        var createdColumn = new TableColumn<LobbyViewModel, String>("Started At");
        createdColumn.setCellValueFactory(new PropertyValueFactory<>("created"));
        createdColumn.setPrefWidth(100);
        createdColumn.setStyle("-fx-font-weight: bold;");
        getColumns().add(createdColumn);

        var contextMenu = new ContextMenu();

        var spectateMenuItem = new MenuItem("Spectate Match");
        spectateMenuItem.setOnAction(k -> {
            var match = getSelectionModel().getSelectedItem();
            if (match != null)
                Aoe2Service.spectateGame(match.getId());
        });
        contextMenu.getItems().add(spectateMenuItem);

        setContextMenu(contextMenu);
    }
}
