package ui.factories;

import domain.CountryCodeService;
import domain.model.Player;
import domain.model.Slot;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import ui.AppResources;
import ui.model.SlotViewModel;
import ui.viewmodel.LobbyViewModel;
import ui.controls.PlayerRatingLabel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class LobbyPanel extends HBox {
    public LobbyPanel(LobbyViewModel viewModel) {
        if (viewModel != null) {
            var slots = viewModel.getPlayers();
            if (slots != null) {
                var addedTeam = false;

                var teams = new HashMap<Integer, List<SlotViewModel>>();
                for (SlotViewModel slot : slots) {
                    var team = slot.getSlot().team().get();
                    var ls = teams.computeIfAbsent(team, k -> new ArrayList<>());
                    ls.add(slot);
                }

                for (var team : teams.keySet()) {
                    if (addedTeam)
                        getChildren().add(new Text(" VS "));

                    boolean addedPlayer = false;

                    for (var slotViewModel : teams.get(team)) {
                        var p = slotViewModel.getSlot().player();
                        if (p.isPresent()) {
                            Player player = p.get();
                            if (addedPlayer)
                                getChildren().add(new Text(", "));
                            var hbox = new HBox();
                            hbox.setSpacing(2);

                            if (slotViewModel.getSlot().won().orElse(false)) {
                                var imageView = new ImageView(AppResources.WonIcon);
                                imageView.setFitWidth(16);
                                imageView.setFitHeight(16);
                                hbox.getChildren().add(imageView);
                            }

                            var countryImage = AppResources.getCountryFlag(player.country());
                            if (countryImage.isPresent()) {
                                var imageView = new ImageView(countryImage.get());
                                Tooltip.install(imageView, new Tooltip(CountryCodeService.getCountry(player.country())));
                                imageView.setFitWidth(16);
                                imageView.setFitHeight(16);
                                hbox.getChildren().add(imageView);
                            }
                            hbox.getChildren().add(new PlayerRatingLabel(player));
                            getChildren().add(hbox);
                            addedPlayer = true;
                        }
                    }
                    addedTeam = true;
                }
            }
        }
    }
}

public class PlayersColumnCellFactory implements Callback<TableColumn<LobbyViewModel, List<Slot>>, TableCell<LobbyViewModel, List<Slot>>> {
    @Override
    public TableCell<LobbyViewModel, List<Slot>> call(TableColumn<LobbyViewModel, List<Slot>> column) {
        return new TableCell<>() {
            @Override
            protected void updateItem(List<Slot> slots, boolean empty) {
                super.updateItem(slots, empty);
                setGraphic(empty ? null : new LobbyPanel(getTableRow().getItem()));
            }
        };
    }
}
