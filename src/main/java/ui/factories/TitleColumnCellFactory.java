package ui.factories;

import domain.model.Lobby;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import ui.AppResources;
import ui.viewmodel.LobbyViewModel;

public class TitleColumnCellFactory implements Callback<TableColumn<LobbyViewModel, Lobby>, TableCell<LobbyViewModel, Lobby>> {
    @Override
    public TableCell<LobbyViewModel, Lobby> call(TableColumn<LobbyViewModel, Lobby> column) {
        return new TableCell<>() {
            @Override
            protected void updateItem(Lobby lobby, boolean empty) {
                super.updateItem(lobby, empty);
                var row = getTableRow().getItem();
                if (empty || row == null) {
                    setGraphic(null);
                } else {
                    var hbox = new HBox();
                    hbox.setSpacing(2);
                    if (row.getLobby().passwordProtected()) {
                        var imageView = new ImageView(AppResources.KeyIcon);
                        imageView.setFitHeight(16);
                        imageView.setFitWidth(16);
                        hbox.getChildren().add(imageView);
                    }
                    var txt = row.getTitle();
                    if (row.getLobby().ranked())
                        txt = "[Ranked] " + txt;
                    hbox.getChildren().add(new Label(txt));
                    setGraphic(hbox);
                }
            }
        };
    }
}
