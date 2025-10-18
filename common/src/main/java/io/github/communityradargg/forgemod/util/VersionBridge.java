package io.github.communityradargg.forgemod.util;

import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.UUID;

public interface VersionBridge {
    @NotNull String getVersion();

    void addMessageToChat(final @NotNull String message);

    boolean isNotInWorld();

    Optional<UUID> getPlayerUuidByNameFromWorld(final @NotNull String playerName);
}
