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
import io.github.communityradargg.forgemod.util.Utils;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A class containing a listener for player name formatting.
 */
public class PlayerNameFormatListener {
    private final CommonHandler commonHandler;

    /**
     * Constructs the class {@link PlayerNameFormatListener}.
     *
     * @param commonHandler The common handler.
     */
    public PlayerNameFormatListener(final @NotNull CommonHandler commonHandler) {
        this.commonHandler = commonHandler;
    }

    /**
     * The listener for the {@link PlayerEvent.NameFormat} event.
     *
     * @param event The event.
     */
    @SubscribeEvent
    public void onPlayerNameFormat(final PlayerEvent.NameFormat event) {
        if (!commonHandler.isOnGrieferGames()) {
            return;
        }
        Utils.updatePlayerNameTag(commonHandler, event.getEntityPlayer(), commonHandler.getListManager().getExistingPrefixes());
    }
}
