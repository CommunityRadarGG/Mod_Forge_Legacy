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

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * A class with some util methods.
 */
public class Utils {
    /**
     * Gets a {@link NetworkPlayerInfo} by the uuid of a player.
     *
     * @param uuid The uuid to get the network player info for.
     * @return Returns an optional with the network player info of an online player to the uuid.
     */
    private static @NotNull Optional<NetworkPlayerInfo> getNetworkPlayerInfoByUuid(final @NotNull UUID uuid) {
        return Minecraft.getMinecraft().thePlayer.sendQueue.getPlayerInfoMap().stream()
                .filter(player -> player.getGameProfile() != null && uuid.equals(player.getGameProfile().getId()))
                .findFirst();
    }

    /**
     * Gets a {@link EntityPlayer} by the uuid of a player.
     *
     * @param uuid The uuid to get the entity player for.
     * @return Returns an optional with the entity player to the uuid.
     */
    private static @NotNull Optional<EntityPlayer> getEntityPlayerByUuid(final @NotNull UUID uuid) {
        final World world = Minecraft.getMinecraft().theWorld;
        if (world == null) {
            return Optional.empty();
        }

        return world.playerEntities.stream()
                .filter(player -> player.getGameProfile() != null && uuid.equals(player.getGameProfile().getId()))
                .findFirst();
    }

    /**
     * Updates a player display name and name tag by its uuid.
     *
     * @param commonHandler The common handler.
     * @param uuid The uuid to update the corresponding player.
     */
    public static void updatePlayerByUuid(final @NotNull CommonHandler commonHandler, final @NotNull UUID uuid, final @NotNull Set<String> oldPrefixes) {
        getEntityPlayerByUuid(uuid).ifPresent(player -> updatePlayerNameTag(commonHandler, player, oldPrefixes));
        getNetworkPlayerInfoByUuid(uuid).ifPresent(networkPlayerInfo -> updatePlayerPrefix(commonHandler, networkPlayerInfo, oldPrefixes));
    }

    /**
     * Handles updating the name tag of a player entity.
     *
     * @param commonHandler The common handler.
     * @param player The player entity to update the name tag.
     * @param oldPrefixes The old prefixes that need to be removed before adding the new one.
     */
    public static void updatePlayerNameTag(final @NotNull CommonHandler commonHandler, final @NotNull EntityPlayer player, final @NotNull Set<String> oldPrefixes) {
        player.getPrefixes().removeIf(prefix -> commonHandler.isPrefixMatching(prefix.getUnformattedText(), oldPrefixes));
        final String addonPrefix = commonHandler.getListManager()
                .getPrefix(player.getGameProfile().getId());

        if (!addonPrefix.isEmpty()) {
            player.addPrefix(new ChatComponentText(commonHandler.formatPrefix(addonPrefix)));
        }
    }

    /**
     * Handles updating the player prefixes in the display name.
     *
     * @param commonHandler The common handler.
     * @param oldPrefixes The old prefixes that need to be removed before adding the new one.
     */
    public static void updatePrefixes(final @NotNull CommonHandler commonHandler, final @NotNull Set<String> oldPrefixes) {
        Minecraft.getMinecraft().thePlayer.sendQueue.getPlayerInfoMap()
                .forEach(player -> updatePlayerPrefix(commonHandler, player, oldPrefixes));
    }

    /**
     * Handles updating the player prefix in the display name of a single player.
     *
     * @param commonHandler The common handler.
     * @param player The player to update.
     * @param oldPrefixes The old prefixes that need to be removed before adding the new one.
     */
    private static void updatePlayerPrefix(final @NotNull CommonHandler commonHandler, final @NotNull NetworkPlayerInfo player, final @NotNull Set<String> oldPrefixes) {
        if (player.getGameProfile().getId() == null || player.getDisplayName() == null) {
            return;
        }

        final IChatComponent displayName = player.getDisplayName();
        IChatComponent newDisplayName = displayName;
        for (final String prefix : oldPrefixes) {
            if (!displayName.getUnformattedText().startsWith(commonHandler.unformatPrefixForCompare(prefix))) {
                continue;
            }
            newDisplayName = displayName.getSiblings().get(displayName.getSiblings().size() - 1);
        }

        final String addonPrefix = commonHandler.getListManager()
                .getPrefix(player.getGameProfile().getId());
        if (!addonPrefix.isEmpty()) {
            newDisplayName = new ChatComponentText(commonHandler.formatPrefix(addonPrefix)).appendSibling(newDisplayName);
        }
        player.setDisplayName(newDisplayName);
    }
}
