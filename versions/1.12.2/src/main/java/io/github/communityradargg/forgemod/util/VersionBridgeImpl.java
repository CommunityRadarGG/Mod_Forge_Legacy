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

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The version bridge implementation for the 1.12.2 version.
 */
public class VersionBridgeImpl implements VersionBridge {
    private static final Logger LOGGER = LogManager.getLogger(VersionBridgeImpl.class);

    @Override
    public @NotNull String getVersion() {
        final ModContainer modContainer = Loader.instance().getIndexedModList().get(CommonHandler.MOD_ID);
        if (modContainer == null) {
            return "UNKNOWN";
        }

        return modContainer.getVersion();
    }

    @Override
    public void addMessageToChat(final @NotNull String message) {
        if (Minecraft.getMinecraft().player == null) {
            LOGGER.warn("Could not add message to chat. Player is null. The message is following: {}", message);
            return;
        }

        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(message));
    }

    @Override
    public boolean isNotInWorld() {
        return Minecraft.getMinecraft().world == null;
    }

    @Override
    public @NotNull List<@NotNull PlayerInfo> getWorldPlayers() {
        final NetHandlerPlayClient connection = Minecraft.getMinecraft().getConnection();
        if (connection == null) {
            return Collections.emptyList();
        }

        return connection.getPlayerInfoMap().stream()
                .map(networkPlayerInfo -> {
                    final GameProfile gameProfile = networkPlayerInfo.getGameProfile();
                    return new PlayerInfo(gameProfile.getId(), gameProfile.getName());
                })
                .collect(Collectors.toList());
    }

    @Override
    public void updatePlayerByUuid(final @NotNull CommonHandler commonHandler, final @NotNull UUID uuid, final @NotNull Set<@NotNull String> oldPrefixes) {
        Utils.updatePlayerByUuid(commonHandler, uuid, oldPrefixes);
    }

    @Override
    public void updatePrefixes(final @NotNull CommonHandler commonHandler, final @NotNull Set<String> oldPrefixes) {
        Utils.updatePrefixes(commonHandler, oldPrefixes);
    }

    @Override
    public boolean isPlayerListKeyPressed() {
        return Minecraft.getMinecraft().gameSettings.keyBindPlayerList.isPressed();
    }

    @Override
    public @NotNull String wrapAndUnformatText(final @NotNull String text) {
        return new TextComponentString(text).getUnformattedText();
    }
}
