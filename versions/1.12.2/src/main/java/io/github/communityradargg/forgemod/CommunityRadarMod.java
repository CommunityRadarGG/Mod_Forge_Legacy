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
package io.github.communityradargg.forgemod;

import io.github.communityradargg.forgemod.command.RadarCommand;
import io.github.communityradargg.forgemod.event.ClientChatReceivedListener;
import io.github.communityradargg.forgemod.event.ClientConnectionDisconnectListener;
import io.github.communityradargg.forgemod.event.KeyInputListener;
import io.github.communityradargg.forgemod.event.PlayerNameFormatListener;
import io.github.communityradargg.forgemod.util.CommonHandler;
import io.github.communityradargg.forgemod.util.VersionBridgeImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents the main class of the mod.
 */
@Mod(modid = CommonHandler.MOD_ID)
public class CommunityRadarMod {
    private static final Logger LOGGER = LogManager.getLogger(CommunityRadarMod.class);
    private final CommonHandler commonHandler = new CommonHandler(new VersionBridgeImpl());

    /**
     * The listener for the {@link FMLInitializationEvent} event.
     *
     * @param event The event.
     */
    @EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("Loading the mod '{}'", CommonHandler.MOD_ID);

        registerEvents();
        registerCommands();
        LOGGER.info("Successfully loaded the mod '{}'", CommonHandler.MOD_ID);
    }

    /**
     * Registers the events.
     */
    private void registerEvents() {
        MinecraftForge.EVENT_BUS.register(new ClientChatReceivedListener(commonHandler));
        MinecraftForge.EVENT_BUS.register(new PlayerNameFormatListener(commonHandler));
        MinecraftForge.EVENT_BUS.register(new KeyInputListener(commonHandler));
        MinecraftForge.EVENT_BUS.register(new ClientConnectionDisconnectListener(commonHandler));
    }

    /**
     * Registers the commands.
     */
    private void registerCommands() {
        ClientCommandHandler.instance.registerCommand(new RadarCommand(commonHandler));
    }

    @Deprecated
    public void sendMessage(final @NotNull String message) {
        if (Minecraft.getMinecraft().player == null) {
            LOGGER.warn("Could not add message to chat. Player is null. The message is following: {}", message);
            return;
        }

        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(message));
    }
}
