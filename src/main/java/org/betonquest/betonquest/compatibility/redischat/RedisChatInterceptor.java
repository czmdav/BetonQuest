package org.betonquest.betonquest.compatibility.redischat;

import dev.unnm3d.redischat.api.RedisChatAPI;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.Interceptor;

import java.util.Objects;

/**
 * Chat Interceptor that works with RedisChat.
 */
public class RedisChatInterceptor extends Interceptor {
    /**
     * RedisChatAPI instance.
     */
    private final RedisChatAPI api;

    /**
     * Creates an interceptor for RedisChat.
     * Stops the chat on conversation start and resumes it on conversation end,
     * sending all the missed messages to the player.
     *
     * @param conv          Conversation to intercept
     * @param onlineProfile OnlineProfile of the player
     */
    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "false positive: Objects.requireNonNull(...)")
    public RedisChatInterceptor(final Conversation conv, final OnlineProfile onlineProfile) {
        super(conv, onlineProfile.getPlayer());
        this.api = Objects.requireNonNull(RedisChatAPI.getAPI());
        api.pauseChat(player);
    }

    @Override
    public void end() {
        api.unpauseChat(player);
    }
}
