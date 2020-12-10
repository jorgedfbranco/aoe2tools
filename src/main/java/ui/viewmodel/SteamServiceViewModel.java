package ui.viewmodel;

import domain.SteamService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

public class SteamServiceViewModel {
    public static final ObservableSet<Long> SteamFriends = FXCollections.observableSet();

    public static void refreshFriends(long steamId) {
        var service = new SteamService();
        var steamIds = service.getFriendsSteamIds(steamId);
        SteamFriends.clear();
        SteamFriends.addAll(steamIds);
    }
}
