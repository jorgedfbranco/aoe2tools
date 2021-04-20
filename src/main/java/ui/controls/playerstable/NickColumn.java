package ui.controls.playerstable;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import ui.controls.PlayerLabel;
import ui.model.PlayerRow;

public class NickColumn extends TableColumn<PlayerRow, String> {
    public void onPlayerLabelCreated(PlayerLabel label) { }

    public NickColumn() {
        setPrefWidth(200);
        setCellValueFactory(new PropertyValueFactory<>("nick"));
        setCellFactory(new Callback<>() {
            @Override
            public TableCell<PlayerRow, String> call(TableColumn<PlayerRow, String> playerRowStringTableColumn) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String v, boolean empty) {
                        super.updateItem(v, empty);
                        setText("");
                        setGraphic(null);
                        var player = getTableRow().getItem();
                        if (player != null) {
                            var playerLabel = new PlayerLabel(player.player, false, false);
                            onPlayerLabelCreated(playerLabel);
                            setGraphic(playerLabel);
                        }
                    }
                };
            }
        });
    }
}
