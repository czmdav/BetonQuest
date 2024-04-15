package org.betonquest.betonquest.conversation;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.util.Index;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.config.Config;

import java.util.Locale;

import static net.kyori.adventure.text.format.Style.style;

/**
 * Holds the styles of the conversations.
 * Meant as a replacement for {@link ConversationColors}.
 */
@UtilityClass
public class ConversationStyles {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuest.getInstance().getLoggerFactory().create(ConversationColors.class);

    /**
     * Index to search for {@link TextDecoration} by name. Easier to handle than {@link TextDecoration#valueOf(String)}.
     */
    private static final Index<String, TextDecoration> TEXT_DECORATION_NAME_INDEX = Index.create(Enum::name, TextDecoration.values());

    /**
     * Style for npc names.
     */
    @Getter
    private static Style npcStyle;

    /**
     * Style for player names.
     */
    @Getter
    private static Style playerStyle;

    /**
     * Style for npc text.
     */
    @Getter
    private static Style textStyle;

    /**
     * Style for player text.
     */
    @Getter
    private static Style answerStyle;

    /**
     * Style for option number.
     */
    @Getter
    private static Style numberStyle;

    /**
     * Style for option text.
     */
    @Getter
    private static Style optionStyle;

    /**
     * Loads all styles
     */
    public static void loadColors() {
        textStyle = getStyle("config.conversation_colors.text");
        npcStyle = getStyle("config.conversation_colors.npc");
        playerStyle = getStyle("config.conversation_colors.player");
        numberStyle = getStyle("config.conversation_colors.number");
        answerStyle = getStyle("config.conversation_colors.answer");
        optionStyle = getStyle("config.conversation_colors.option");
    }

    /**
     * <p>Retrieves a style from the configuration.
     *
     * @param configKey the config address
     * @return the parsed style
     */
    private static Style getStyle(final String configKey) {
        final var configValue = Config.getString(configKey).split(",");
        final var styleBuilder = style();
        // Set all decorations to false by default. Required because of style inheritance in Components.
        for (final var decoration : TextDecoration.values()) {
            styleBuilder.decoration(decoration, false);
        }
        for (final var s : configValue) {
            updateStyle(styleBuilder, s);
        }
        return styleBuilder.build();
    }

    /**
     * <p>Updates a style builder with the given format. Format can be the name of a color or a text decoration.
     *
     * @param style  the builder to update
     * @param format the format to parse
     * @return the input builder
     */
    private static void updateStyle(final Style.Builder style, final String format) {
        final var formatName = format.toUpperCase(Locale.ROOT).trim().replace(" ", "_");
        final var color = NamedTextColor.NAMES.value(formatName);
        if (color != null) {
            style.color(color);
            return;
        }
        final var decor = TEXT_DECORATION_NAME_INDEX.value(formatName);
        if (decor != null) {
            style.decorate(decor);
            return;
        }
        LOG.warn("Could not parse conversation style '%s'. It is not recognised as a color or text decoration.".formatted(format));
    }

}
