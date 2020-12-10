package domain;

import infra.Settings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

public class AppSettingsViewModel {
    public static final SimpleStringProperty FilterString = new SimpleStringProperty();
    public static final SimpleBooleanProperty ShowCountryFlags = new SimpleBooleanProperty();
    public static final SimpleBooleanProperty ShowAutomatches = new SimpleBooleanProperty();
    public static final SimpleBooleanProperty AutoRefreshLobbyListing = new SimpleBooleanProperty();
    public static final SimpleBooleanProperty ShowFavouritesOnly = new SimpleBooleanProperty();
    public static final SimpleBooleanProperty ColorPlayersRating = new SimpleBooleanProperty();
    public static final SimpleBooleanProperty ShowRatings = new SimpleBooleanProperty();
    public static final ObservableSet<String> FavouriteMaps = FXCollections.observableSet();
    public static final ObservableSet<Long> FollowedUsers = FXCollections.observableSet();
    public static final ObservableMap<Long, String> Notes = FXCollections.observableHashMap();

    static {
        Settings.handleStringSetting("filter", FilterString);

        Settings.handleBooleanSetting("showFlags", ShowCountryFlags);
        Settings.handleBooleanSetting("colorPlayersRating", ColorPlayersRating);
        Settings.handleBooleanSetting("showRatings", ShowRatings);
        Settings.handleBooleanSetting("showAutomatches", ShowAutomatches);
        Settings.handleBooleanSetting("showFavouritesOnly", ShowFavouritesOnly);
        Settings.handleBooleanSetting("autoRefreshLobbyListing", AutoRefreshLobbyListing);

        Settings.handleStringSetSetting("favouriteMaps", FavouriteMaps);
        Settings.handleLongSetSetting("followedUsers", FollowedUsers);

        Settings.handleLongStringHashMap("notes", Notes);
    }
}
