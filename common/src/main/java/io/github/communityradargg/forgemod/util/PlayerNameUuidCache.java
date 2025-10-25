/*
 * Copyright 2024 - present CommunityRadarGG <https://community-radar.de/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
