package io.github.communityradargg.forgemod.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

/**
 * A class with some general util methods.
 */
public class GeneralUtils {
    private static final DateTimeFormatter readableDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private static boolean onGrieferGames = false;

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
    public static boolean isGrieferGamesHostName(final @NotNull String hostName) {
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
    public static @NotNull String formatDateTime(final @NotNull LocalDateTime localDateTime) {
        return localDateTime.format(readableDateTimeFormatter);
    }

    /**
     * Gets the GrieferGames connection state.
     *
     * @return Returns the GrieferGames connection state.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isOnGrieferGames() {
        return onGrieferGames;
    }

    /**
     * Sets the GrieferGames connection state.
     *
     * @param onGrieferGames The GrieferGames connection state to set.
     */
    public static void setOnGrieferGames(final boolean onGrieferGames) {
        GeneralUtils.onGrieferGames = onGrieferGames;
    }

    public static void setOnGrieferGames(final boolean isLocal, final @Nullable SocketAddress socketAddress) {
        if (isLocal) {
            return;
        }

        if (!(socketAddress instanceof InetSocketAddress)) {
            return;
        }

        final String hostname = ((InetSocketAddress) socketAddress).getHostName();
        if (GeneralUtils.isGrieferGamesHostName(hostname)) {
            GeneralUtils.setOnGrieferGames(true);
            return;
        }
        GeneralUtils.setOnGrieferGames(false);
    }
}
