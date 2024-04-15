package org.betonquest.betonquest.utils;

import net.kyori.adventure.text.Component;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

/**
 * <p>Utilities for converting legacy text formatting to <a href="https://docs.advntr.dev/minimessage/format.html">
 * MiniMessage format</a>.</p>
 */
public final class AdventureComponentConverter {

    /**
     * Pattern for legacy text formatting. Includes colors and text decorations.
     */
    private static final Pattern LEGACY_TEXT_FORMAT_PATTERN = Pattern.compile("(?i)[&§]([0-9a-fk-or])");

    /**
     * Pattern for legacy hex color format.
     */
    private static final Pattern LEGACY_HEX_COLOR_PATTERN = Pattern.compile("(?i)[&§]x(([&§][0-9a-fk-or]){6})");

    /**
     * <p>Mappings of legacy format codes to the equivalent <a href="https://docs.advntr.dev/minimessage/format.html">
     * MiniMessage format</a>.</p>
     * <p><b>Note</b>: In legacy coloring, when introducing a new color code, the text decorations will reset. However,
     * this behaviour is not reproduced by using the MiniMessage format. To mimic the legacy behaviour, an extra
     * {@code <reset>} tag is prepended to every converted color format.</p>
     */
    private static final Map<String, String> LEGACY_TO_MINI_MESSAGE = Map.ofEntries(
            Map.entry("0", "<reset><black>"),
            Map.entry("1", "<reset><dark_blue>"),
            Map.entry("2", "<reset><dark_green>"),
            Map.entry("3", "<reset><dark_aqua>"),
            Map.entry("4", "<reset><dark_red>"),
            Map.entry("5", "<reset><dark_purple>"),
            Map.entry("6", "<reset><gold>"),
            Map.entry("7", "<reset><gray>"),
            Map.entry("8", "<reset><dark_gray>"),
            Map.entry("9", "<reset><blue>"),
            Map.entry("a", "<reset><green>"),
            Map.entry("b", "<reset><aqua>"),
            Map.entry("c", "<reset><red>"),
            Map.entry("d", "<reset><light_purple>"),
            Map.entry("e", "<reset><yellow>"),
            Map.entry("f", "<reset><white>"),
            Map.entry("k", "<obf>"),
            Map.entry("l", "<b>"),
            Map.entry("m", "<st>"),
            Map.entry("n", "<u>"),
            Map.entry("o", "<i>"),
            Map.entry("r", "<reset>")
    );

    private AdventureComponentConverter() {
    }

    /**
     * <p>Checks for any legacy text formatting in the input String.</p>
     *
     * @param format The input to check for legacy formats
     * @return {@code true} when the input string has any legacy format codes.
     */
    public static boolean hasLegacyFormat(final String format) {
        return LEGACY_TEXT_FORMAT_PATTERN.matcher(format).find();
    }

    /**
     * <p>This method converts legacy colors and text decorations to
     * <a href="https://docs.advntr.dev/minimessage/format.html">MiniMessage format</a>.</p>
     *
     * @param format Text with legacy format to convert
     * @return The input format converted to MiniMessage format.
     * @see #legacyToComponent(String)
     */
    public static String legacyToString(final String format) {
        if (format == null) {
            return null;
        }
        var miniMessageFormat = format;
        // Replace legacy hex formats first
        var matcher = LEGACY_HEX_COLOR_PATTERN.matcher(miniMessageFormat);
        while (matcher.find()) {
            // Note: To mimic the legacy behaviour, an extra <reset> tag is prepended to every converted hex color.
            miniMessageFormat = miniMessageFormat.replaceAll(matcher.group(), "<reset><#" + matcher.group(1).replaceAll("[&§]", "") + ">");
            matcher = LEGACY_HEX_COLOR_PATTERN.matcher(miniMessageFormat);
        }
        // Replace legacy colors and formatting
        matcher = LEGACY_TEXT_FORMAT_PATTERN.matcher(miniMessageFormat);
        while (matcher.find()) {
            final var replacement = LEGACY_TO_MINI_MESSAGE.get(matcher.group(1).toLowerCase(Locale.ROOT));
            if (replacement != null) {
                miniMessageFormat = miniMessageFormat.replaceAll(matcher.group(), replacement);
                matcher = LEGACY_TEXT_FORMAT_PATTERN.matcher(miniMessageFormat);
            }
        }
        return miniMessageFormat;
    }

    /**
     * <p>This method converts legacy colors and text decorations to
     * <a href="https://docs.advntr.dev/index.html">Adventure components</a>.</p>
     *
     * @param format Text with legacy format to convert
     * @return The input format converted to Adventure components.
     * @see #legacyToString(String)
     */
    public static Component legacyToComponent(final String format) {
        if (format == null) {
            return null;
        }
        return miniMessage().deserialize(legacyToString(format));
    }

}
