package ui.controls.matchestable;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ui.viewmodel.LobbyViewModel;

public class CreatedColumn extends TableColumn<LobbyViewModel, String> {
    public CreatedColumn() {
        super("Started At");
        setCellValueFactory(new PropertyValueFactory<>("created"));
        setPrefWidth(100);
        setStyle("-fx-font-weight: bold;");
    }
}
