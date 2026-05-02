package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class StringUtils {

    private StringUtils() {
    }

    public static String formatLogTime(Date time) {
        return new SimpleDateFormat("MM.dd HH:mm:ss").format(time);
    }

    public static String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        String trimmed = value.trim();
        return trimmed.substring(0, 1).toUpperCase() + trimmed.substring(1).toLowerCase();
    }
}
