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

import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * An interface holding all methods, which need a version specific implementation.
 */
public interface VersionBridge {
    /**
     * Gets the mod version.
     *
     * @return Returns the mod version.
     */
    @NotNull String getVersion();

    /**
     * Adds a message to the player chat.
     *
     * @param message The message.
     */
    void addMessageToChat(final @NotNull String message);

    /**
     * Checks if the player is not in a world.
     *
     * @return Returns {@code true} if the player is not in a world, else {@code false}.
     */
    boolean isNotInWorld();

    /**
     * Gets a list with player wrappers with their game profile UUID and name for all players in the current world.
     *
     * @return Returns the list with player wrappers with all players in the world.
     */
    @NotNull List<@NotNull PlayerInfo> getWorldPlayers();

    /**
     * Updates a player by its UUID.
     *
     * @param commonHandler The common handler.
     * @param uuid The player UUID.
     * @param oldPrefixes A Set with old prefixes.
     */
    void updatePlayerByUuid(final @NotNull CommonHandler commonHandler, final @NotNull UUID uuid, final @NotNull Set<@NotNull String> oldPrefixes);

    /**
     * Updates the prefixes for all players.
     *
     * @param commonHandler The common handler.
     * @param oldPrefixes A Set with old prefixes.
     */
    void updatePrefixes(final @NotNull CommonHandler commonHandler, final @NotNull Set<String> oldPrefixes);

    /**
     * Checks is the player list key is pressed.
     *
     * @return Returns {@code true} if the key is pressed, else {@code false}.
     */
    boolean isPlayerListKeyPressed();

    /**
     * Wraps a given text in the version specific text component and unformat it for later possible comparison.
     *
     * @param text The text.
     * @return Returns the unformatted text.
     */
    @NotNull String wrapAndUnformatText(final @NotNull String text);
}
