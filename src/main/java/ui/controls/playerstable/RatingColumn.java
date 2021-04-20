package ui.controls.playerstable;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import ui.model.PlayerRow;
import ui.model.RatingColor;

public class RatingColumn extends TableColumn<PlayerRow, Integer> {
    public static RatingColumn _1x1RatingColumn = new RatingColumn("1x1", "_1x1Rating");
    public static RatingColumn tgRatingColumn = new RatingColumn("TG", "tgRating");
    public static RatingColumn unrankedRatingColumn = new RatingColumn("Unranked", "unranked");

    protected RatingColumn(String title, String field) {
        super(title);
        setCellValueFactory(new PropertyValueFactory<>(field));
        setCellFactory(new Callback<>() {
            @Override
            public TableCell<PlayerRow, Integer> call(TableColumn<PlayerRow, Integer> column) {
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
        });
    }
}
