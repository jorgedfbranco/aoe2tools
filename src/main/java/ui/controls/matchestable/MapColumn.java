package ui.controls.matchestable;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ui.viewmodel.LobbyViewModel;

public class MapColumn extends TableColumn<LobbyViewModel, String> {
    public MapColumn() {
        super("Map");
        setCellValueFactory(new PropertyValueFactory<>("mapType"));
        setStyle("-fx-font-weight: bold; -fx-alignment: center;");
        setPrefWidth(125);
    }
}
