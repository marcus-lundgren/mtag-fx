package helper;

import javafx.scene.paint.Color;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

public class ColorHelper {
    private final MessageDigest digest;
    private static final Color ACTIVITY_ENTRY_IS_ACTIVE_BACKGROUND_COLOR = Color.color(0.54d, 0.85d, 0.54d);
    private static final Color ACTIVITY_ENTRY_IS_INACTIVE_BACKGROUND_COLOR = Color.color(0.5d, 0.5d, 0.5d);
    private static final Color ACTIVITY_ENTRY_IS_ACTIVE_TEXT_COLOR = Color.color(0.12d, 0.93d, 0.12d);
    private static final Color ACTIVITY_ENTRY_IS_INACTIVE_TEXT_COLOR = Color.color(0.75d, 0.75d, 0.75d);

    public ColorHelper() {
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new RuntimeException("Should not happen. MD5 not present?");
        }
    }

    public Color toColor(boolean isActive) {
        return isActive ? ACTIVITY_ENTRY_IS_ACTIVE_BACKGROUND_COLOR : ACTIVITY_ENTRY_IS_INACTIVE_BACKGROUND_COLOR;
    }

    public Color toTextColor(boolean isActive) {
        return isActive ? ACTIVITY_ENTRY_IS_ACTIVE_TEXT_COLOR : ACTIVITY_ENTRY_IS_INACTIVE_TEXT_COLOR;
    }

    public Color toColor(String s) {
        final var hash = digest.digest(s.getBytes(StandardCharsets.UTF_8));
        final var hashAsBase64 = HexFormat.of().formatHex(hash);

        final int startIndex = 1;
        final int space = 2;
        final var red = calculateColorValue(startIndex, hashAsBase64);
        final var green = calculateColorValue(startIndex + space, hashAsBase64);
        final var blue = calculateColorValue(startIndex + space * 2, hashAsBase64);

        return Color.color(red, green, blue);
    }

    private static double calculateColorValue(int startIndex, String hexString) {
        final var colorHexString = hexString.substring(startIndex, startIndex + 2);
        return Integer.parseInt(colorHexString, 16) / 255d;
    }
}
