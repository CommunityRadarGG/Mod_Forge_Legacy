package io.github.communityradargg.forgemod.util;

import org.jetbrains.annotations.NotNull;
import java.util.List;

public interface VersionBridge {
    @NotNull String getVersion();

    void addMessageToChat(final @NotNull String message);

    boolean isNotInWorld();

    @NotNull List<@NotNull PlayerInfo> getWorldPlayers();
}
