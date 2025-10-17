package io.github.communityradargg.forgemod;

import org.jetbrains.annotations.NotNull;

public interface ModSpecific {
    String MOD_ID = "communityradar";

    /**
     * Gets the version.
     *
     * @return Returns the version.
     */
    String getVersion();

    void sendMessage(final @NotNull String message);
}
