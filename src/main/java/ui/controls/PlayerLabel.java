package ui.controls;

import domain.AppSettingsViewModel;
import domain.CountryCodeService;
import domain.SteamService;
import domain.model.Player;
import infra.Browser;
import infra.WindowsClipboard;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.StageStyle;
import ui.AppResources;

public class PlayerLabel extends HBox {
    public PlayerLabel(Player player) {
        setSpacing(2);
        setStyle("-fx-font-weight: bold;");
        var countryImage = AppResources.getCountryFlag(player.country());
        if (countryImage.isPresent()) {
            var imageView = new ImageView(countryImage.get());
            Tooltip.install(imageView, new Tooltip(CountryCodeService.getCountry(player.country())));
            imageView.setFitWidth(16);
            imageView.setFitHeight(16);
            imageView.visibleProperty().bindBidirectional(AppSettingsViewModel.ShowCountryFlags);
            imageView.managedProperty().bindBidirectional(AppSettingsViewModel.ShowCountryFlags);
            getChildren().add(imageView);
        }

        var playerLabel = new Label(player.name());
        setOnMouseEntered(k -> playerLabel.setUnderline(true));
        setOnMouseExited(k -> playerLabel.setUnderline(false));

        var contextMenu = new ContextMenu();

        var steamProfileMenu = new MenuItem("Open Steam Profile");
        steamProfileMenu.setDisable(player.steamId() == 0);
        var steamProfileImageView = new ImageView(AppResources.SteamIcon);
        steamProfileImageView.setFitWidth(12);
        steamProfileImageView.setFitHeight(12);
        steamProfileMenu.setGraphic(steamProfileImageView);
        var steamUrl = "https://steamcommunity.com/profiles/" + player.steamId();
        steamProfileMenu.setOnAction(k -> Browser.open(steamUrl));
        contextMenu.getItems().add(steamProfileMenu);

        var aoe2ProfileMenu = new MenuItem("Open aoe2.net Profile");
        var aoe2ProfileImageView = new ImageView(AppResources.Aoe2NetIcon);
        aoe2ProfileImageView.setFitWidth(12);
        aoe2ProfileImageView.setFitHeight(12);
        aoe2ProfileMenu.setGraphic(aoe2ProfileImageView);
        var aoe2Url = "https://aoe2.net/#profile-" + player.id().id();
        aoe2ProfileMenu.setOnAction(k -> Browser.open(aoe2Url));
        contextMenu.getItems().add(aoe2ProfileMenu);

        var aoe2ClubProfileMenu = new MenuItem("Open aoe2.club Profile");
        var aoe2ClubProfileImageView = new ImageView(AppResources.Aoe2ClubIcon);
        aoe2ClubProfileImageView.setFitWidth(12);
        aoe2ClubProfileImageView.setFitHeight(12);
        aoe2ClubProfileMenu.setGraphic(aoe2ClubProfileImageView);
        var aoe2ClubUrl = "https://aoe2.club/playerprofile/" + player.id().id();
        aoe2ClubProfileMenu.setOnAction(k -> Browser.open(aoe2ClubUrl));
        contextMenu.getItems().add(aoe2ClubProfileMenu);

        var aoe2InsightsProfileMenu = new MenuItem("Open aoe2-insights Profile");
        var aoe2InsightsUrl = "https://www.aoe2insights.com/user/" + player.id().id();
        var aoe2InsightsImageView = new ImageView(AppResources.Aoe2InsightsIcon);
        aoe2InsightsImageView.setFitWidth(12);
        aoe2InsightsImageView.setFitHeight(12);
        aoe2InsightsProfileMenu.setGraphic(aoe2InsightsImageView);
        aoe2InsightsProfileMenu.setOnAction(k -> Browser.open(aoe2InsightsUrl));
        contextMenu.getItems().add(aoe2InsightsProfileMenu);

        var alsoKnownAsMenu = new MenuItem("Also known as...");
        alsoKnownAsMenu.setDisable(player.steamId() == 0);
        var alsoKnownAsImageView = new ImageView(AppResources.SteamIcon);
        alsoKnownAsImageView.setFitWidth(12);
        alsoKnownAsImageView.setFitHeight(12);
        alsoKnownAsMenu.setGraphic(alsoKnownAsImageView);
        alsoKnownAsMenu.setOnAction(k -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Known aliases");
            alert.setHeaderText(null);
            alert.initStyle(StageStyle.UTILITY);
            var aliases = new SteamService().getPastAliases(player.steamId());
            alert.setContentText(aliases.isEmpty() ? player.name() : String.join("\r\n", aliases));
            alert.showAndWait();
        });
        contextMenu.getItems().add(alsoKnownAsMenu);

        contextMenu.getItems().add(new SeparatorMenuItem());

        var copyNicknameMenu = new MenuItem("Copy Nickname");
        copyNicknameMenu.setOnAction(k -> WindowsClipboard.setClipboard(String.valueOf(player.name())));
        contextMenu.getItems().add(copyNicknameMenu);

        var copySteamIdMenu = new MenuItem("Copy Steam Id - " + player.steamId());
        copySteamIdMenu.setDisable(player.steamId() == 0);
        copySteamIdMenu.setOnAction(k -> WindowsClipboard.setClipboard(String.valueOf(player.steamId())));
        contextMenu.getItems().add(copySteamIdMenu);

        contextMenu.getItems().add(new SeparatorMenuItem());
        playerLabel.setContextMenu(contextMenu);

        getChildren().add(playerLabel);
    }
}
