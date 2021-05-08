package ui.controls.matchestable;

import domain.Aoe2DotNetService;
import domain.model.ProfileId;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import ui.viewmodel.LobbyViewModel;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ExecutorService;


/**
 * A TableView displaying one match (or lobby) per row.
 */
public class MatchesTable extends TableView<LobbyViewModel> {
    private final ObservableList<LobbyViewModel> matches = FXCollections.observableList(new ArrayList<>());

    private final ObjectProperty<ProfileId> profileId = new SimpleObjectProperty<>();
    public ObjectProperty<ProfileId> playerIdProperty() { return profileId; }

    private final ExecutorService threadpool;

    public MatchesTable(ExecutorService threadpool) {
        this.threadpool = threadpool;
        setPlaceholder(new Label());
        setItems(matches);
        setContextMenu(new ContextMenu());
    }

    public void showMatches(ProfileId id) {
        matches.clear();
        this.profileId.set(id);
        var progress = new ProgressIndicator();
        progress.setMaxWidth(24);
        setPlaceholder(progress);
        threadpool.submit(() -> {
            var lobbies = Aoe2DotNetService.getMatches(id);
            Platform.runLater(() -> {
                lobbies.forEach(lobby -> matches.add(new LobbyViewModel(lobby, Optional.empty())));
                if (matches.isEmpty())
                    setPlaceholder(new Label("No matches found for player."));
                else
                    getSelectionModel().select(0);
            });
        });
    }

    public void clear() {
        matches.clear();
        this.profileId.set(null);
    }
}
