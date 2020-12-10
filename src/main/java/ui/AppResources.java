package ui;

import javafx.scene.image.Image;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class AppResources {
    public static Image FullHeart = new Image(AppResources.class.getResourceAsStream("/heart-full.png"));
    public static Image EmptyHeart = new Image(AppResources.class.getResourceAsStream("/heart-empty.png"));
    public static Image Aoe2NetIcon = new Image(AppResources.class.getResourceAsStream("/aoe2net_icon.png"));
    public static Image Aoe2ClubIcon = new Image(AppResources.class.getResourceAsStream("/aoe2club_icon.png"));
    public static Image SteamIcon = new Image(AppResources.class.getResourceAsStream("/steam.png"));
    public static Image Aoe2InsightsIcon = new Image(AppResources.class.getResourceAsStream("/aoe2insights.jpg"));
    public static Image InfoIcon = new Image(AppResources.class.getResourceAsStream("/info-32.png"));
    public static Image KeyIcon = new Image(AppResources.class.getResourceAsStream("/key-red.png"));
    public static Image NoteIcon = new Image(AppResources.class.getResourceAsStream("/note.png"));
    public static Image WonIcon = new Image(AppResources.class.getResourceAsStream("/winner.png"));

    private static Map<String, Optional<Image>> flags = new ConcurrentHashMap<>();

    public static Optional<Image> getCountryFlag(String countryCode) {
        var flag = flags.get(countryCode);
        if (flag == null) {
            var filename = "/flags/24x24/" + countryCode.toUpperCase() + ".png";
            var stream = AppResources.class.getResourceAsStream(filename);

            Optional<Image> image = Optional.empty();
            if (stream != null)
                image = Optional.of(new Image(stream));
            flags.put(countryCode, image);
            return image;
        } else {
            return flag;
        }
    }
}
