package infra;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
    public static DateTimeFormatter DefaultFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime toLocalDateTime(long time) {
        return LocalDateTime.ofEpochSecond(time, 0, OffsetDateTime.now().getOffset());
    }

    public static String humanizedDuration(LocalDateTime time) {
        var duration = Duration.between(time, LocalDateTime.now());
        var days = duration.toDaysPart();
        var hours = duration.toHoursPart();
        var mins = duration.toMinutesPart();
        var seconds = duration.toSecondsPart();

        if (days == 0 && hours == 0 && mins == 0)
            return "just now";

        StringBuilder sb = new StringBuilder();
        if (days > 0)
            sb.append(days + "d");
        if (hours > 0)
            sb.append(hours + "h");
        if (mins > 0)
            sb.append(mins + "m");
        if (seconds > 0)
            sb.append(seconds + "s");
        return sb.toString() + " ago";
    }
}
