package io.github.communityradargg.forgemod.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.communityradargg.forgemod.list.ListManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

/**
 * A class for handling many utility and central tasks and holds the version bridge.
 */
public class CommonHandler {
    public static final String MOD_ID = "communityradar";
    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private static final String MOJANG_API_NAME_TO_UUID = "https://api.mojang.com/users/profiles/minecraft/";
    private static final Pattern UUID_MOJANG_API_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");
    private final DateTimeFormatter readableDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private final Map<String, UUID> UUID_NAME_CACHE = new HashMap<>();
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
     * Tries to get the uuid to the player name from the world.
     *
     * @param commonHandler The common handler.
     * @param playerName The player name to get the corresponding uuid.
     * @return Returns a CompletableFuture with an optional with the player uuid.
     */
    public @NotNull CompletableFuture<Optional<UUID>> getUuidByPlayerName(final @NotNull CommonHandler commonHandler, final @NotNull String playerName) {
        // user has to be in a world
        if (versionBridge.isNotInWorld()) {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        // If the UUID has been cached, returning from the map.
        if (UUID_NAME_CACHE.containsKey(playerName)) {
            return CompletableFuture.completedFuture(Optional.of(UUID_NAME_CACHE.get(playerName)));
        }

        // Checking if there is a player with same name in the loaded world. If so, returning UUID from EntityPlayer.
        for (final PlayerInfo playerInfo : versionBridge.getWorldPlayers()) {
            if (playerInfo.getPlayerName().equalsIgnoreCase(playerName) && playerInfo.getUuid() != null) {
                UUID_NAME_CACHE.put(playerName, playerInfo.getUuid());
                return CompletableFuture.completedFuture(Optional.of(playerInfo.getUuid()));
            }
        }

        if (playerName.startsWith("!") || playerName.startsWith("~")) {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        // If no player with same name is in the world, try fetching the UUID from the Mojang-API.
        return commonHandler.requestUuidForName(playerName);
    }

    /**
     * Requests an uuid to a player name, from the Mojang API.
     *
     * @param playerName The player name to get the uuid for.
     * @return Returns a CompletableFuture with an optional with the requested uuid, it will be empty if an error occurred on requesting.
     */
    private @NotNull CompletableFuture<Optional<UUID>> requestUuidForName(final @NotNull String playerName) {
        final String urlText = MOJANG_API_NAME_TO_UUID + playerName;
        return CompletableFuture.supplyAsync(() -> {
            HttpURLConnection connection = null;
            try {
                final URL url = new URL(urlText);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(3000);
                connection.setReadTimeout(3000);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", CommonHandler.MOD_ID + "/" + versionBridge.getVersion());

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    LOGGER.warn("Requesting data from '{}' resulted in following status code: {}", urlText, connection.getResponseCode());
                    return Optional.empty();
                }

                try (final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    final JsonObject json = new Gson().fromJson(reader, JsonObject.class);
                    if (json == null || !json.has("id") || !json.has("name")) {
                        connection.disconnect();
                        return Optional.empty();
                    }

                    final UUID uuid = UUID.fromString(UUID_MOJANG_API_PATTERN.matcher(json.get("id").getAsString()).replaceAll("$1-$2-$3-$4-$5"));
                    UUID_NAME_CACHE.put(playerName, uuid);
                    connection.disconnect();
                    return Optional.of(uuid);
                }
            } catch (final Exception e) {
                if (connection != null) {
                    connection.disconnect();
                }
                LOGGER.error("Trying to request data from '{}' resulted in an exception", urlText, e);
                return Optional.empty();
            }
        });
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
     * Adds a chat message to the player chat.
     *
     * @param message The message.
     */
    public void addMessageToChat(final @NotNull String message) {
        versionBridge.addMessageToChat(message);
    }

    /**
     * Gets the player info data for all players in the current world.
     *
     * @return Returns the player info data.
     */
    public @NotNull List<@NotNull PlayerInfo> getWorldPlayers() {
        return versionBridge.getWorldPlayers();
    }

    /**
     * Updates a player display name and name tag by its uuid.
     *
     * @param uuid The uuid to update the corresponding player.
     * @param oldPrefixes The old prefixes.
     */
    public void updatePlayerByUuid(final @NotNull UUID uuid, final @NotNull Set<String> oldPrefixes) {
        versionBridge.updatePlayerByUuid(this, uuid, oldPrefixes);
    }

    /**
     * Handles the key input event.
     */
    public void handleKeyInputEvent() {
        if (onGrieferGames && versionBridge.isPlayerListKeyPressed()) {
            versionBridge.updatePrefixes(this, listManager.getExistingPrefixes());
        }
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
