package ui.viewmodel;

import domain.Aoe2DotNetService;
import domain.Aoe2Service;
import domain.AppSettingsViewModel;
import domain.model.Lobby;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import ui.model.SlotViewModel;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class LobbyBrowserViewModel {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final ObservableList<LobbyViewModel> observableLobbies = FXCollections.observableArrayList(new ArrayList<>());
    public final FilteredList<LobbyViewModel> filteredLobbies = new FilteredList<>(observableLobbies);
    public final SortedList<LobbyViewModel> sortedLobbies = new SortedList<>(filteredLobbies);

    private Optional<LobbyViewModel> selectedLobby = Optional.empty();

    // TODO: This is currently never getting cleaned up, so this will lead to a memory leak...
    private final Map<Long, Optional<LocalDateTime>> creationTimes = new ConcurrentHashMap<>();
    private boolean hasRefreshedLobbies = false;

    public LobbyBrowserViewModel() {
        AppSettingsViewModel.FilterString.addListener(k -> updateFilter());
        AppSettingsViewModel.ShowAutomatches.addListener(k -> updateFilter());
        AppSettingsViewModel.ShowFavouritesOnly.addListener(k -> updateFilter());
        AppSettingsViewModel.FavouriteMaps.addListener((InvalidationListener) observable -> updateFilter());

        scheduler.scheduleWithFixedDelay(() -> {
            refreshLobbies();
        },0, 30, TimeUnit.SECONDS);
    }

    private void updateFilter() {
        filteredLobbies.setPredicate(new Predicate<LobbyViewModel>() {
            @Override
            public boolean test(LobbyViewModel row) {
                if (AppSettingsViewModel.ShowFavouritesOnly.get() && !AppSettingsViewModel.FavouriteMaps.contains(row.getMapType()))
                    return false;
                if (!AppSettingsViewModel.ShowAutomatches.get() && row.getTitle().equals("AUTOMATCH"))
                    return false;
                String filter = AppSettingsViewModel.FilterString.get();
                if (filter.trim().isEmpty())
                    return true;
                String[] filters = filter.toLowerCase().split(",");

                for (String s : filters) {
                    String filterWord = s.toLowerCase().trim();
                    if (!filterWord.isEmpty()) {
                        String title = row.getTitle() != null ? row.getTitle().toLowerCase() : "";
                        String mapType = row.getMapType() != null ? row.getMapType().toLowerCase() : "";
                        if (title.contains(filterWord) || mapType.contains(filterWord))
                            return true;
                        for (SlotViewModel slot : row.getPlayers()) {
                            if (slot.getSlot().player().isPresent()) {
                                String player = slot.getSlot().player().get().name().toLowerCase();
                                if (player.contains(filterWord))
                                    return true;
                            }
                        }
                    }
                }

                return false;
            }
        });
    }

    public void refreshLobbies() {
        scheduler.execute(() -> {
            var newLobbyListing = Aoe2DotNetService.getLobbies();

            for (var lobby : newLobbyListing)
                if (!creationTimes.containsKey(lobby.id()))
                    creationTimes.put(lobby.id(), !hasRefreshedLobbies ? Optional.empty() : Optional.of(LocalDateTime.now()));
            hasRefreshedLobbies = true;

            Platform.runLater(() -> {
                observableLobbies.clear();
                for (Lobby lobby : newLobbyListing)
                    observableLobbies.add(new LobbyViewModel(lobby, creationTimes.get(lobby.id())));
                updateFilter();
            });
        });
    }

    public void joinLobby(long lobbyId) {
        Aoe2Service.joinLobby(lobbyId);
    }

    public void setSelectedLobby(LobbyViewModel lobby) {
        selectedLobby = Optional.of(lobby);
    }
}
