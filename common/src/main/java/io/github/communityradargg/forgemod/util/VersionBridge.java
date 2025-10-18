package io.github.communityradargg.forgemod.util;

import org.jetbrains.annotations.NotNull;

public interface VersionBridge {
    @NotNull String getVersion();

    void addMessageToChat(final @NotNull String message);
}
