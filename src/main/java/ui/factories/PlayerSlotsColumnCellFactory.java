package ui.factories;

import domain.model.Lobby;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import ui.viewmodel.LobbyViewModel;

public class PlayerSlotsColumnCellFactory implements Callback<TableColumn<LobbyViewModel, Lobby>, TableCell<LobbyViewModel, Lobby>> {
    @Override
    public TableCell<LobbyViewModel, Lobby> call(TableColumn<LobbyViewModel, Lobby> column) {
        return new TableCell<>() {
            @Override
            protected void updateItem(Lobby lobby, boolean empty) {
            super.updateItem(lobby, empty);
                if (empty || lobby == null) {
                    setText("");
                    setGraphic(null);
                } else {
                    setText(lobby.numPlayers() + "/" + lobby.slotCount());
                    var percentage = 1.0 * lobby.numPlayers() / lobby.slotCount();
                    Color color;
                    if (percentage >= 1.0) {
                        color = Color.RED;
                    } else if (percentage >= 0.75) {
                        color = Color.DARKGOLDENROD;
                    } else {
                        color = Color.GREEN;
                    }
                    setTextFill(color);
                    setStyle("-fx-font-weight: bold;");
                }
            }
        };
    }
}
