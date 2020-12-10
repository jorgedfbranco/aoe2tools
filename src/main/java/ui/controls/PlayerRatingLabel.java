package ui.controls;

import domain.model.Player;
import infra.Browser;
import infra.WindowsClipboard;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import ui.AppResources;

public class PlayerRatingLabel extends Label {
    public PlayerRatingLabel(Player player) {
        var viewModel = new PlayerRatingLabelViewModel(player);
        textProperty().bind(viewModel.Text);
        styleProperty().bind(viewModel.Style);

        setOnMouseEntered(k -> setUnderline(true));
        setOnMouseExited(k -> setUnderline(false));

        var contextMenu = new ContextMenu();

//        var joinLobbyMenu = new MenuItem("Join Lobby");
//        joinLobbyMenu.setOnAction(k -> Aoe2Service.joinLobby(lobbyId));
//        contextMenu.getItems().add(joinLobbyMenu);
//
//        contextMenu.getItems().add(new SeparatorMenuItem());

        var steamProfileMenu = new MenuItem("Open Steam Profile");
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
        var aoe2ClubUrl = "https://aoe2.club/playerprofile/" + player.id();
        aoe2ClubProfileMenu.setOnAction(k -> Browser.open(aoe2ClubUrl));
        contextMenu.getItems().add(aoe2ClubProfileMenu);

        var aoe2InsightsProfileMenu = new MenuItem("Open aoe2-insights Profile");
        var aoe2InsightsUrl = "https://www.aoe2insights.com/user/" + player.id();
        var aoe2InsightsImageView = new ImageView(AppResources.Aoe2InsightsIcon);
        aoe2InsightsImageView.setFitWidth(12);
        aoe2InsightsImageView.setFitHeight(12);
        aoe2InsightsProfileMenu.setGraphic(aoe2InsightsImageView);
        aoe2InsightsProfileMenu.setOnAction(k -> Browser.open(aoe2InsightsUrl));
        contextMenu.getItems().add(aoe2InsightsProfileMenu);

//        contextMenu.getItems().add(new SeparatorMenuItem());
//
//        var fullMenu = new MenuItem("Full Info");
//        fullMenu.setOnAction(k -> {
//            var gridPane = new GridPane();
//
//            var playerScene = new Scene(gridPane, 400, 400);
//
//            var playerStage = new Stage();
//            playerStage.initModality(Modality.APPLICATION_MODAL);
//            playerStage.setScene(playerScene);
//            playerStage.showAndWait();
//        });
//        contextMenu.getItems().add(fullMenu);

        contextMenu.getItems().add(new SeparatorMenuItem());

        var copyNicknameMenu = new MenuItem("Copy Nickname");
        copyNicknameMenu.setOnAction(k -> WindowsClipboard.setClipboard(String.valueOf(player.name())));
        contextMenu.getItems().add(copyNicknameMenu);

        var copySteamIdMenu = new MenuItem("Copy Steam Id - " + player.steamId());
        copySteamIdMenu.setOnAction(k -> WindowsClipboard.setClipboard(String.valueOf(player.steamId())));
        contextMenu.getItems().add(copySteamIdMenu);

//        contextMenu.getItems().add(new SeparatorMenuItem());

//        var followMenuItem = new MenuItem();
//        if (AppSettingsViewModel.FollowedUsers.contains(player.steamId())) {
//            followMenuItem.setText("Unfollow");
//        } else {
//            followMenuItem.setText("Follow");
//        }
//        followMenuItem.setOnAction(k -> {
//            if (AppSettingsViewModel.FollowedUsers.contains(player.steamId())) {
//                AppSettingsViewModel.FollowedUsers.remove(player.steamId());
//                followMenuItem.setText("Follow");
//            } else {
//                AppSettingsViewModel.FollowedUsers.add(player.steamId());
//                followMenuItem.setText("Unfollow");
//            }
//        });
//        contextMenu.getItems().add(followMenuItem);
//        contextMenu.getItems().add(new SeparatorMenuItem());

//        var addNoteMenu = new MenuItem("Add note");
//        var noteImageView = new ImageView(AppResources.NoteIcon);
//        noteImageView.setFitHeight(12);
//        noteImageView.setFitWidth(12);
//        addNoteMenu.setGraphic(noteImageView);
//        addNoteMenu.setOnAction(actionEvent -> {
//            var stage = new Stage();
//            var parentWindow = getScene().getWindow();
//            stage.initModality(Modality.WINDOW_MODAL);
//            stage.initOwner(parentWindow);
//            stage.setTitle("Add / Edit Player Notes");
//            stage.setWidth(600);
//            stage.setHeight(300);
//            stage.setX(parentWindow.getX() + parentWindow.getWidth() / 2 - stage.getWidth() / 2);
//            stage.setY(parentWindow.getY() + parentWindow.getHeight() / 2 - stage.getHeight() / 2);
//
//            // There seems to be some sort of bug here. if the application's is running on a computer
//            // with multiple screens, if one moves this application's main dialog from its original
//            // screen to another and then attempts to run this code, it will crash with a
//            // "Exception in thread "JavaFX Application Thread" java.lang.NullPointerException: Cannot invoke "com.sun.glass.ui.View.updateLocation()" because "this.view" is null"
//            // Why is that so? I don't know..
//            stage.showAndWait();
//        });
//        contextMenu.getItems().add(addNoteMenu);

        setContextMenu(contextMenu);
    }
}