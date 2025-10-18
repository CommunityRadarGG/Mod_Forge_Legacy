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
package io.github.communityradargg.forgemod.event;

import io.github.communityradargg.forgemod.util.CommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A class containing listeners for player connect and disconnect from and to servers.
 */
public class ClientConnectionDisconnectListener {
    private final CommonHandler commonHandler;

    /**
     * Constructs a {@link CommonHandler}.
     *
     * @param commonHandler Constructs a common handler.
     */
    public ClientConnectionDisconnectListener(final @NotNull CommonHandler commonHandler) {
        this.commonHandler = commonHandler;
    }

    /**
     * The listener for the {@link FMLNetworkEvent.ClientConnectedToServerEvent} event.
     *
     * @param event The event.
     */
    @SubscribeEvent
    public void onFMLNetworkClientConnectedToServer(final FMLNetworkEvent.ClientConnectedToServerEvent event) {
        commonHandler.setOnGrieferGames(event.isLocal, event.manager.getRemoteAddress());
    }

    /**
     * The listener for the {@link FMLNetworkEvent.ClientDisconnectionFromServerEvent} event.
     *
     * @param event The event.
     */
    @SubscribeEvent
    public void onFMLNetworkClientDisconnectionFromServer(final FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        commonHandler.setOnGrieferGames(false);
    }
}
