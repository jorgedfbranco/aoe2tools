package ui.factories;

import domain.AppSettingsViewModel;
import javafx.collections.SetChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import ui.AppResources;
import ui.viewmodel.LobbyViewModel;

public class MapColumnCellFactory implements Callback<TableColumn<LobbyViewModel, String>, TableCell<LobbyViewModel, String>> {
    @Override
    public TableCell<LobbyViewModel, String> call(TableColumn<LobbyViewModel, String> column) {
        return new TableCell<>() {
            @Override
            protected void updateItem(String map, boolean empty) {
                super.updateItem(map, empty);
                setText("");
                if (empty) {
                    setGraphic(null);
                } else {
                    var hbox = new HBox();
                    hbox.setSpacing(2);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    var imageView = new ImageView();
                    imageView.setPickOnBounds(true);
                    imageView.setFitWidth(12);
                    imageView.setFitHeight(12);
                    if (AppSettingsViewModel.FavouriteMaps.contains(map))
                        imageView.setImage(AppResources.FullHeart);

                    imageView.setOnMouseEntered(k -> {
                        if (imageView.getImage() == null)
                            imageView.setImage(AppResources.EmptyHeart);
                    });
                    imageView.setOnMouseExited(k -> {
                        if (!AppSettingsViewModel.FavouriteMaps.contains(map))
                            imageView.setImage(null);
                    });

                    // the approach used below is quite inefficient as is, but i'll let
                    // it like this for now..
                    AppSettingsViewModel.FavouriteMaps.addListener((SetChangeListener<String>) change -> {
                        if (AppSettingsViewModel.FavouriteMaps.contains(map)) {
                            imageView.setImage(AppResources.FullHeart);
                        } else {
                            imageView.setImage(null);
                        }
                    });
                    imageView.setOnMouseClicked(k -> {
                        if (AppSettingsViewModel.FavouriteMaps.contains(map)) {
                            AppSettingsViewModel.FavouriteMaps.remove(map);
                        } else {
                            AppSettingsViewModel.FavouriteMaps.add(map);
                        }
                    });

                    hbox.getChildren().add(imageView);
                    hbox.getChildren().add(new Label(map));
                    setGraphic(hbox);
                }
            }
        };
    }
}