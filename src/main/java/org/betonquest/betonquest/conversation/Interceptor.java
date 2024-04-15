package org.betonquest.betonquest.conversation;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.ComponentLike;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

/**
 * The interceptor is used to intercept chat messages that are sent to the player.
 * This is useful to provide a distraction free conversation experience.
 * This class does no interceptions, but subclasses do.
 */
@RequiredArgsConstructor
public class Interceptor {
    /**
     * The conversation this interceptor acts for.
     */
    protected final Conversation conv;

    /**
     * The player whose chat is being intercepted.
     */
    protected final Player player;

    /**
     * Send message to player bypassing Interceptor
     *
     * @param message the message
     */
    public void sendMessage(final String message) {
        sendMessage(TextComponent.fromLegacyText(message));
    }

    /**
     * Send message to player bypassing Interceptor
     *
     * @param message the message
     */
    public void sendMessage(final ComponentLike message) {
        player.sendMessage(message);
    }

    /**
     * Send message to player bypassing Interceptor
     *
     * @param message the message
     */
    public void sendMessage(final BaseComponent... message) {
        player.spigot().sendMessage(message);
    }

    /**
     * Ends the work of this interceptor
     */
    public void end() {
        // Empty
    }
}
