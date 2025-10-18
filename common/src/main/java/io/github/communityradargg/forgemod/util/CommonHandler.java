package io.github.communityradargg.forgemod.util;

import io.github.communityradargg.forgemod.list.ListManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

/**
 * A class for handling many utility and central tasks and holds the version bridge.
 */
public class CommonHandler {
    public static final String MOD_ID = "communityradar";
    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private final DateTimeFormatter readableDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private final VersionBridge versionBridge;
    private final ListManager listManager;
    private boolean onGrieferGames = false;

    /**
     * Constructs a {@link CommonHandler}.
     *
     * @param versionBridge A version bridge implementation
     */
    public CommonHandler(final @NotNull VersionBridge versionBridge) {
        this.versionBridge = versionBridge;

        listManager = new ListManager(this);
        registerPublicLists();
        // Needs to be after loading public lists
        listManager.loadPrivateLists();
    }

    /**
     * Registers the public lists.
     */
    private void registerPublicLists() {
        if (!listManager.registerPublicList("scammer", "&7[&cScammer&7]", "https://lists.community-radar.de/versions/v1/scammer.json")) {
            LOGGER.error("Could not register public list 'scammers'!");
        }

        if (!listManager.registerPublicList("trusted", "&7[&aTrusted&7]", "https://lists.community-radar.de/versions/v1/trusted.json")) {
            LOGGER.error("Could not register public list 'verbvllert_trusted'!");
        }
    }

    /**
     * Checks if a given hostname is a hostname of GrieferGames.
     * <br><br>
     * Following domains are taken into account:
     * <br>
     * - griefergames.net
     * <br>
     * - griefergames.de
     * <br>
     * - griefergames.live
     *
     * @param hostName The hostname to check.
     * @return Returns, whether the given hostname is one of the GrieferGames hostnames.
     */
    public boolean isGrieferGamesHostName(final @NotNull String hostName) {
        final String filteredHostName = Optional.of(hostName)
                .filter(host -> host.endsWith("."))
                .map(host -> host.substring(0, host.length() - 1).toLowerCase(Locale.ENGLISH))
                .orElse(hostName.toLowerCase(Locale.ENGLISH));
        return filteredHostName.endsWith("griefergames.net") || filteredHostName.endsWith("griefergames.de") || filteredHostName.endsWith("griefergames.live");
    }

    /**
     * Formats a given date time in a human-readable form.
     *
     * @param localDateTime The local date time to format.
     * @return Returns the formatted date time.
     */
    public @NotNull String formatDateTime(final @NotNull LocalDateTime localDateTime) {
        return localDateTime.format(readableDateTimeFormatter);
    }

    /**
     * Gets the GrieferGames connection state.
     *
     * @return Returns the GrieferGames connection state.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isOnGrieferGames() {
        return onGrieferGames;
    }

    /**
     * Sets the GrieferGames connection state.
     *
     * @param onGrieferGames The GrieferGames connection state to set.
     */
    public void setOnGrieferGames(final boolean onGrieferGames) {
        this.onGrieferGames = onGrieferGames;
    }

    public void setOnGrieferGames(final boolean isLocal, final @Nullable SocketAddress socketAddress) {
        if (isLocal) {
            return;
        }

        if (!(socketAddress instanceof InetSocketAddress)) {
            return;
        }

        final String hostname = ((InetSocketAddress) socketAddress).getHostName();
        if (isGrieferGamesHostName(hostname)) {
            onGrieferGames = true;
            return;
        }
        onGrieferGames = false;
    }

    /**
     * Gets the mod version.
     *
     * @return Returns the mod version.
     */
    public @NotNull String getVersion() {
        return versionBridge.getVersion();
    }

    /**
     * Gets the list manager.
     *
     * @return Returns the list manager.
     */
    public ListManager getListManager() {
        return listManager;
    }
}
