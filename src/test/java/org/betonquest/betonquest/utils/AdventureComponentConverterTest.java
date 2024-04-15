package org.betonquest.betonquest.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for class {@link AdventureComponentConverter}.
 */
class AdventureComponentConverterTest {

    private static Stream<Arguments> legacyToMiniMessage() {
        return Stream.of(
                // No format
                Arguments.of("", ""),
                Arguments.of("Bist du bereit?", "Bist du bereit?"),
                // all legacy formats
                Arguments.of("§0Should be black", "<reset><black>Should be black"),
                Arguments.of("§1Should be dark blue", "<reset><dark_blue>Should be dark blue"),
                Arguments.of("§2Should be dark green", "<reset><dark_green>Should be dark green"),
                Arguments.of("§3Should be dark aqua", "<reset><dark_aqua>Should be dark aqua"),
                Arguments.of("§4Should be dark red", "<reset><dark_red>Should be dark red"),
                Arguments.of("§5Should be dark purple", "<reset><dark_purple>Should be dark purple"),
                Arguments.of("§6Should be gold", "<reset><gold>Should be gold"),
                Arguments.of("§7Should be gray", "<reset><gray>Should be gray"),
                Arguments.of("§8Should be dark gray", "<reset><dark_gray>Should be dark gray"),
                Arguments.of("§9Should be blue", "<reset><blue>Should be blue"),
                Arguments.of("§aShould be green", "<reset><green>Should be green"),
                Arguments.of("§BShould be aqua", "<reset><aqua>Should be aqua"),
                Arguments.of("§cShould be red", "<reset><red>Should be red"),
                Arguments.of("§DShould be light purple", "<reset><light_purple>Should be light purple"),
                Arguments.of("§eShould be yellow", "<reset><yellow>Should be yellow"),
                Arguments.of("§FShould be white", "<reset><white>Should be white"),
                Arguments.of("§kShould be obfuscated", "<obf>Should be obfuscated"),
                Arguments.of("§LShould be bold", "<b>Should be bold"),
                Arguments.of("§mShould be strikethrough", "<st>Should be strikethrough"),
                Arguments.of("§NShould be underlined", "<u>Should be underlined"),
                Arguments.of("§oShould be italic", "<i>Should be italic"),
                Arguments.of("§RShould reset", "<reset>Should reset"),
                // mixed '§' and '&' formats
                Arguments.of("§0Bist &4du §6bereit?", "<reset><black>Bist <reset><dark_red>du <reset><gold>bereit?"),
                Arguments.of("&aActive Quest: §aFlint", "<reset><green>Active Quest: <reset><green>Flint"),
                // hex colors
                Arguments.of("§x§1§a§2§b§3§cLower case hex", "<reset><#1a2b3c>Lower case hex"),
                Arguments.of("&x&1&A&2&B&3&CUppercase hex", "<reset><#1A2B3C>Uppercase hex")
        );
    }

    @ParameterizedTest
    @MethodSource("legacyToMiniMessage")
    void legacyFormatConvert(final String input, final String expected) {
        final var converted = AdventureComponentConverter.legacyToString(input);

        assertEquals(expected, converted, "The converted message does not match the expected message.");
        assertFalse(AdventureComponentConverter.hasLegacyFormat(converted), "The converted message has legacy formatting.");
    }

}
