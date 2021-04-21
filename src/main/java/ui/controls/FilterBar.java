package ui.controls;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class FilterBar extends HBox {
    private final Label label;
    private final TextField textField = new TextField();
    private final Button button = new Button("Search");
    private final BooleanProperty searchButtonDisabled = new SimpleBooleanProperty(true);

    public FilterBar(String caption) {
        this.label = new Label(caption);
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(8);
        getChildren().add(label);
        getChildren().add(textField);
        getChildren().add(button);
        HBox.setHgrow(textField, Priority.ALWAYS);

        textField.setOnKeyPressed(k -> {
            if (k.getCode() == KeyCode.ENTER) {
                button.fire();
            }
        });

        button.disableProperty().bind(searchButtonDisabled);
        button.setOnAction(k -> onFilter(textField.getText()));
    }

    public BooleanProperty searchButtonDisabledProperty() {
        return searchButtonDisabled;
    }

    protected void onFilter(String filter) {}
}