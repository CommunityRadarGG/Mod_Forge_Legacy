package io.github.communityradargg.forgemod.util;

import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface VersionBridge {
    @NotNull String getVersion();

    void addMessageToChat(final @NotNull String message);

    boolean isNotInWorld();

    @NotNull List<@NotNull PlayerInfo> getWorldPlayers();

    void updatePlayerByUuid(final @NotNull CommonHandler commonHandler, final @NotNull UUID uuid, final @NotNull Set<@NotNull String> oldPrefixes);
}
