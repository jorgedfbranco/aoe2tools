package domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import domain.model.Rating;
import domain.model.RatingType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RatingsService {
    private static final Path cachePath = Paths.get(".player_ratings");
    private static final ObjectMapper mapper =
        new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(new Jdk8Module());

    private static final Map<String, RatingInformation> ratings = new ConcurrentHashMap<>();
    private static final Executor executor = Executors.newFixedThreadPool(4);

    static {
        try {
            if (Files.exists(cachePath)) {
                var tree = mapper.readTree(cachePath.toFile());
                Iterator<JsonNode> iterator = tree.elements();
                while (iterator.hasNext()) {
                    var ratingInformation = mapper.treeToValue(iterator.next(), RatingInformation.class);
                    ratings.put(getKey(ratingInformation.steamId, ratingInformation.type), ratingInformation);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Optional<Rating> getRatingsFromMemory(long steamId, RatingType type) {
        var ratingInformation = ratings.get(getKey(steamId, type));
        if (ratingInformation == null) return Optional.empty();
        return ratingInformation.rating;
    }

    /**
     * Gets a key from the cache if there's one, otherwise downloads it.
     * Keys more than K days old will get recomputed again, to avoid having data that is
     * too stale.
     */
    public static CompletableFuture<Optional<Rating>> getRatings(long steamId, RatingType type) {
        var key = getKey(steamId, type);
        var playerRatings = ratings.get(key);
        if (playerRatings == null || playerRatings.accessTime.isBefore(LocalDateTime.now().minusDays(30))) {
            return CompletableFuture.supplyAsync(() -> {
                Optional<Rating> newRating = Aoe2DotNetService.getRating(steamId, type);
                ratings.put(key, new RatingInformation(steamId, type, LocalDateTime.now(), newRating));
                return newRating;
            }, executor);
        } else {
            return CompletableFuture.completedFuture(playerRatings.rating);
        }
    }

    private static String getKey(long steamId, RatingType type) {
        return steamId + ";" + type;
    }

    /**
     * Saves to disk the current contents of our cache to avoid having to always
     * hit aoe2.net so hard. This will then be read at startup.
     */
    public static void saveToDisk() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(cachePath.toFile(), ratings.values());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static record RatingInformation(long steamId, RatingType type, LocalDateTime accessTime, Optional<Rating> rating) { }
}
