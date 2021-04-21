package ui.controls.matchestable;

import domain.Aoe2DotNetService;
import domain.model.SteamId;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import ui.viewmodel.LobbyViewModel;

import java.util.ArrayList;
import java.util.Optional;


/**
 * A TableView displaying one match (or lobby) per row.
 */
public class MatchesTable extends TableView<LobbyViewModel> {
    // TODO: make this private
    public final ObservableList<LobbyViewModel> matches = FXCollections.observableList(new ArrayList<>());

//    public final TableColumn<LobbyViewModel, List<Slot>> playersColumn = new TableColumn<>("Players");

    public MatchesTable() {
        setPlaceholder(new Label());
        setItems(matches);

//
//        var spectateMenuItem = new MenuItem("Spectate Match");
//        spectateMenuItem.setOnAction(k -> {
//            var match = getSelectionModel().getSelectedItem();
//            if (match != null)
//                Aoe2Service.spectateGame(match.getId());
//        });
//        contextMenu.getItems().add(spectateMenuItem);
//
        setContextMenu(new ContextMenu());
    }

    public void showMatches(SteamId steamId) {
        // TODO: use threadpool
        matches.clear();
        var progress = new ProgressIndicator();
        progress.setMaxWidth(24);
        setPlaceholder(progress);
        // TODO: we need to unite this placeholder progressbar logic with the other one of playerListing..
        new Thread(() -> {
            var lobbies = Aoe2DotNetService.getMatches(steamId);
            Platform.runLater(() -> {
                lobbies.forEach(lobby -> matches.add(new LobbyViewModel(lobby, Optional.empty())));
                if (matches.isEmpty())
                    setPlaceholder(new Label("No matches found for player."));
            });
        }).start();
    }
}
