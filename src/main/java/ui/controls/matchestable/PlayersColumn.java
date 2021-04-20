package ui.controls.matchestable;

import domain.model.Player;
import domain.model.Slot;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import ui.AppResources;
import ui.controls.PlayerLabel;
import ui.model.SlotViewModel;
import ui.viewmodel.LobbyViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayersColumn extends TableColumn<LobbyViewModel, List<Slot>> {
    public void onPlayerLabelCreated(PlayerLabel label) {}

    public PlayersColumn() {
        setCellValueFactory(new PropertyValueFactory<>("players"));
        setPrefWidth(1000);
        setStyle("-fx-font-weight: bold;");

        setCellFactory(new Callback<>() {
            @Override
            public TableCell<LobbyViewModel, List<Slot>> call(TableColumn<LobbyViewModel, List<Slot>> column) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(List<Slot> slotsLs, boolean empty) {
                        super.updateItem(slotsLs, empty);
                        if (empty)
                            setGraphic(null);
                        else {
                            var main = new HBox();
                            var viewModel = getTableRow().getItem();
                            if (viewModel != null) {
                                var slots = viewModel.getPlayers();
                                if (slots != null) {
                                    var addedTeam = false;

                                    var teams = new HashMap<Integer, List<SlotViewModel>>();
                                    for (SlotViewModel slot : slots) {
                                        var team = slot.getSlot().team().orElseGet(() -> 0);
                                        var ls = teams.computeIfAbsent(team, k -> new ArrayList<>());
                                        ls.add(slot);
                                    }

                                    for (var team : teams.keySet()) {
                                        if (addedTeam)
                                            main.getChildren().add(new Text(" VS "));

                                        boolean addedPlayer = false;

                                        for (var slotViewModel : teams.get(team)) {
                                            var p = slotViewModel.getSlot().player();
                                            if (p.isPresent()) {
                                                Player player = p.get();
                                                if (addedPlayer)
                                                    main.getChildren().add(new Text(", "));
                                                var hbox = new HBox();
                                                hbox.setSpacing(2);

                                                if (slotViewModel.getSlot().won().orElse(false)) {
                                                    var imageView = new ImageView(AppResources.WonIcon);
                                                    imageView.setFitWidth(16);
                                                    imageView.setFitHeight(16);
                                                    hbox.getChildren().add(imageView);
                                                }

                                                var playerLabel = new PlayerLabel(player, true, true);
                                                hbox.getChildren().add(playerLabel);
                                                main.getChildren().add(hbox);
                                                onPlayerLabelCreated(playerLabel);
                                                addedPlayer = true;
                                            }
                                        }
                                        addedTeam = true;
                                    }
                                }
                            }
                            setGraphic(main);
                        }
                    }
                };
            }
        });
    }
}