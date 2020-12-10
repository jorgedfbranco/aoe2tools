package ui.factories;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import ui.model.RatingColor;

public class RatingColumnCellFactory<T> implements Callback<TableColumn<T, Integer>, TableCell<T, Integer>> {
    @Override
    public TableCell<T, Integer> call(TableColumn<T, Integer> column) {
        return new TableCell<>() {
            @Override
            protected void updateItem(Integer rating, boolean empty) {
                super.updateItem(rating, empty);
                if (empty) {
                    setText("");
                    setGraphic(null);
                } else if (rating != null) {
                    setText(rating != 0 ? String.valueOf(rating) : "");
                    setTextFill(Color.web(RatingColor.color(rating)));
                    setStyle("-fx-font-weight: bold; -fx-alignment: center;");
                }
            }
        };
    }
}
