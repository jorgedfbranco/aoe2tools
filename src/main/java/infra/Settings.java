package infra;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValueFactory;
import javafx.beans.property.Property;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class Settings {
    private static File file = new File(".settings");
    private static Config config;

    static {
        var fallback = ConfigFactory.parseString(ResourcesService.getString("/application.conf"));
        config = ConfigFactory.parseFile(file).withFallback(fallback);
    }

    public static void handleStringSetting(String propertyName, Property<String> property) {
        var render = config.getValue(propertyName).render();
        var str = render.substring(1, render.length()-1);
        property.setValue(str);
        property.addListener(v -> config = config.withValue(propertyName, ConfigValueFactory.fromAnyRef(property.getValue())));
    }

    public static void handleBooleanSetting(String propertyName, Property<Boolean> property) {
        property.setValue(Boolean.valueOf(config.getValue(propertyName).render()));
        property.addListener(v -> config = config.withValue(propertyName, ConfigValueFactory.fromAnyRef(property.getValue())));
    }

    public static void handleStringSetSetting(String propertyName, ObservableSet<String> property) {
        property.addAll(config.getStringList(propertyName));
        property.addListener((SetChangeListener<String>) change -> config = config.withValue(propertyName, ConfigValueFactory.fromIterable(property)));
    }

    public static void handleLongSetSetting(String propertyName, ObservableSet<Long> property) {
        property.addAll(config.getLongList(propertyName));
        property.addListener((SetChangeListener<Long>) change -> config = config.withValue(propertyName, ConfigValueFactory.fromIterable(property)));
    }

    public static void handleLongStringHashMap(String propertyName, ObservableMap<Long, String> property) {
        property.addListener((MapChangeListener<Long, String>) change -> {
            if (change.wasRemoved()) {
                config = config.withoutPath("notes." + change.getKey());
            }
            if (change.wasAdded()) {
                config = config.withValue("notes." + change.getKey() + ".text", ConfigValueFactory.fromAnyRef(change.getValueAdded()));
                config = config.withValue("notes." + change.getKey() + ".time", ConfigValueFactory.fromAnyRef(LocalDateTime.now().format(DateTimeUtils.DefaultFormatter)));
            }
        });
    }

    public static void saveToDisk() {
        try {
            if (Files.exists(file.toPath()))
                Files.delete(file.toPath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        var contents = config.root().render(ConfigRenderOptions.defaults().setOriginComments(false).setJson(false));
        try {
            Files.writeString(file.toPath(), contents, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
