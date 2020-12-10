package domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.model.*;
import infra.DateTimeUtils;
import infra.HttpService;

import java.time.LocalDateTime;
import java.util.*;

public class Aoe2DotNetService {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Map<String, String> strings = getStrings();

    public static Map<String, String> getStrings() {
        String body = HttpService.downloadUrl("https://aoe2.net/api/strings?game=aoe2de&language=en");
        var strings = new HashMap<String, String>();
        try {
            JsonNode tree = objectMapper.readTree(body);
            fillStrings(strings, tree, "age");
            fillStrings(strings, tree, "map_size");
            fillStrings(strings, tree, "map_type");

            fillStrings(strings, tree, "game_type");
            fillStrings(strings, tree, "civ");
            fillStrings(strings, tree, "rating_type");
            fillStrings(strings, tree, "leaderboard");
            fillStrings(strings, tree, "speed");
            fillStrings(strings, tree, "resources");
            fillStrings(strings, tree, "victory");
            fillStrings(strings, tree, "visibility");
            return strings;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void fillStrings(Map<String, String> strings, JsonNode tree, String path) {
        var it = tree.get(path).elements();
        while (it.hasNext()) {
            var element = it.next();
            strings.put(path + "." + element.get("id"), element.get("string").textValue());
        }
    }

    public static List<Lobby> getMatches(long steamId) {
        return getLobbies("https://aoe2.net/api/player/matches?game=aoe2de&steam_id=" + steamId + "&count=1000");
    }

    public static List<Lobby> getLobbies() {
        return getLobbies("https://aoe2.net/api/lobbies?game=aoe2de");
    }

    private static List<Lobby> getLobbies(String url) {
        try {
            String body = HttpService.downloadUrl(url);
            JsonNode tree = objectMapper.readTree(body);
            var iterator = tree.elements();
            List<Lobby> lobbies = new ArrayList<>();
            while (iterator.hasNext()) {
                lobbies.add(parseLobby(iterator.next()));
            }
            return lobbies;
        } catch (Exception e) {
            e.printStackTrace();
            return Arrays.asList();
        }
    }

    private static Player parsePlayer(JsonNode slotElement) {
        return new Player(
            slotElement.get("steam_id").asLong(),
            new ProfileId(slotElement.get("profile_id").asInt()),
            slotElement.get("name").asText(),
            slotElement.get("country").asText(),
            slotElement.get("rating").asInt(),
            slotElement.get("games").asInt(),
            slotElement.get("wins").asInt(),
            slotElement.get("drops").asInt()
        );
    }

    public static Optional<Rating> getRating(long steamId, RatingType type) {
        String url;
        switch (type) {
            case _1x1:
                url = "https://aoe2.net/api/player/ratinghistory?game=aoe2de&leaderboard_id=3&steam_id=" + steamId + "&count=1";
                break;
            case TeamGame:
                url = "https://aoe2.net/api/player/ratinghistory?game=aoe2de&leaderboard_id=4&steam_id=" + steamId + "&count=1";
                break;
            case Unranked:
                url = "https://aoe2.net/api/player/ratinghistory?game=aoe2de&leaderboard_id=0&steam_id=" + steamId + "&count=1";
                break;
            default:
                throw new AssertionError("Unknown rating type");
        }
        String body = HttpService.downloadUrl(url);
        try {
            JsonNode tree = objectMapper.readTree(body);
            var element = tree.get(0);
            if (element == null)
                return Optional.empty();

            var rating = new Rating(
                    element.get("rating").asInt(),
                    element.get("num_wins").asInt(),
                    element.get("num_losses").asInt(),
                    element.get("streak").asInt(),
                    element.get("drops").asInt(),
                    DateTimeUtils.toLocalDateTime(element.get("timestamp").asLong())
            );
            return Optional.of(rating);
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    public static List<Player> findPlayers(String nick, int maxCount, LeaderboardType leardboardType) {
        String body = HttpService.downloadUrl("https://aoe2.net/api/leaderboard?game=aoe2de&leaderboard_id=" + leardboardType.code() + "&start=1&count=" + maxCount + "&search=" + nick);
        var players = new ArrayList<Player>();
        try {
            var tree = objectMapper.readTree(body);
            var iterator = tree.get("leaderboard").elements();
            while (iterator.hasNext()) {
                players.add(parsePlayer(iterator.next()));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return players;
    }

    public static Optional<Lobby> getLastMatch(ProfileId id) {
        String body = HttpService.downloadUrl("https://aoe2.net/api/player/lastmatch?game=aoe2de&profile_id=" + id.id());
        try {
            return Optional.of(parseLobby(objectMapper.readTree(body).get("last_match")));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private static Lobby parseLobby(JsonNode element) {
        List<Slot> slots = new ArrayList<>();
        var playersIterator = element.get("players").iterator();
        while (playersIterator.hasNext()) {
            var slotElement = playersIterator.next();

            SlotType slotType;
            switch (slotElement.get("slot_type").asInt()) {
                case 3: slotType = SlotType.AI; break;
                case 4: slotType = SlotType.Closed; break;
                case 5: slotType = SlotType.Open; break;
                default: slotType = SlotType.Player;
            }

            Optional<Player> player;
            if (slotType == SlotType.Player || slotType == SlotType.AI) {
                player = Optional.of(parsePlayer(slotElement));
            } else {
                player = Optional.empty();
            }

            Optional<Boolean> won = Optional.empty();
            if (!slotElement.get("won").isNull())
                won = Optional.of(slotElement.get("won").asBoolean());

            Optional<Integer> team = Optional.empty();
            if (!slotElement.get("team").isNull())
                team = Optional.of(slotElement.get("team").asInt());

            Optional<Integer> color = Optional.empty();
            if (!slotElement.get("color").isNull())
                color = Optional.of(slotElement.get("color").asInt());

            Optional<String> civ = Optional.empty();
            if (!slotElement.get("civ").isNull())
                civ = Optional.of(strings.get("civ." + slotElement.get("civ").asText()));

            slots.add(new Slot(slotType, player, won, team, civ, color));
        }

        Optional<LocalDateTime> created = Optional.empty();
        var opened = element.get("opened").asLong();
        if (opened != 0)
            created = Optional.of(DateTimeUtils.toLocalDateTime(opened));

        return new Lobby(
            element.get("match_id").asLong(),
            element.get("name").asText().replaceAll("(\n|\r)", ""), // certain lobbies seem to have newlines in them idk why..
            element.get("num_players").asInt(),
            element.get("num_slots").asInt(),
            element.get("has_password").asBoolean(),
            element.get("server").asText(),
            element.get("average_rating").asInt(),
            strings.get("map_size." + element.get("map_size").asText()),
            element.get("pop").asInt(),
            element.get("ranked").asBoolean(),
            strings.get("game_type." + element.get("game_type").asText()),
            strings.get("speed." + element.get("speed").asText()),
            strings.get("visibility." + element.get("visibility").asText()),
            strings.get("map_type." + element.get("map_type").asText()),
            created,
            slots
        );
    }
}