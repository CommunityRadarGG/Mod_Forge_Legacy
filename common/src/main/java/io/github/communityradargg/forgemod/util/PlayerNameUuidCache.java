package io.github.communityradargg.forgemod.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Represents a cache to cache a player name to UUID mapping.
 */
@SuppressWarnings("UnstableApiUsage")
public class PlayerNameUuidCache {
    private final Cache<String, UUID> CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build();

    /**
     * Gets a UUID by a player name out of the cache. The method is case-insensitive.
     *
     * @param playerName The player name to lookup.
     * @return Returns a Optional with the found UUID.
     */
    public Optional<UUID> get(final @NotNull String playerName) {
        return Optional.ofNullable(CACHE.getIfPresent(playerName.toLowerCase(Locale.ENGLISH)));
    }

    /**
     * Puts a player name UUID mapping into the cache.
     *
     * @param playerName The player name.
     * @param uuid The UUID.
     */
    public void put(final @NotNull String playerName, final UUID uuid) {
        CACHE.put(playerName.toLowerCase(Locale.ENGLISH), uuid);
    }

    /**
     * Puts a list of {@link PlayerInfo} into the cache.
     *
     * @param playerInfos The player infos to put in the cache.
     */
    public void putAll(final @NotNull List<@NotNull PlayerInfo> playerInfos) {
        for (final PlayerInfo playerInfo : playerInfos) {
            if (playerInfo.getUuid() == null) {
                continue;
            }

            put(playerInfo.getPlayerName().toLowerCase(Locale.ENGLISH), playerInfo.getUuid());
        }
    }
}
