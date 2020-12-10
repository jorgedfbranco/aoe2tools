package infra;

import java.io.IOException;
import java.util.Optional;

public class ResourcesService {
    public static String getString(String resource) {
        var stream = ResourcesService.class.getResourceAsStream(resource);
        if (stream == null)
            return null;
        try {
            return new String(stream.readAllBytes());
        } catch (IOException e) {
            return null;
        }
    }
}
