package io.github.communityradargg.forgemod.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.UUID;

/**
 * Holds the general player info.
 */
public class PlayerInfo {
    private final UUID uuid;
    private final String playerName;

    /**
     * Constructs a {@link PlayerInfo}.
     *
     * @param uuid The uuid.
     * @param playerName The player name.
     */
    public PlayerInfo(final @Nullable UUID uuid, final @NotNull String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
    }

    /**
     * Gets the uuid.
     *
     * @return Returns the uuid.
     */
    public @Nullable UUID getUuid() {
        return uuid;
    }

    /**
     * Gets the player name.
     *
     * @return Returns the player name.
     */
    public @NotNull String getPlayerName() {
        return playerName;
    }
}
