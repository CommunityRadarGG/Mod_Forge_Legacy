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
package io.github.communityradargg.forgemod.command;

import io.github.communityradargg.forgemod.radarlistmanager.RadarListVisibility;
import io.github.communityradargg.forgemod.util.CommonHandler;
import io.github.communityradargg.forgemod.util.Messages;
import io.github.communityradargg.forgemod.util.RadarMessage;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Holds the logic of the lists subcommand.
 */
public class ListsSubcommand implements Subcommand {
    private final CommonHandler commonHandler;
    private final EntityPlayer player;

    /**
     * Constructs a {@link ListsSubcommand}.
     *
     * @param commonHandler The common handler.
     * @param player The player.
     */
    public ListsSubcommand(final @NotNull CommonHandler commonHandler, final @NotNull EntityPlayer player) {
        this.commonHandler = commonHandler;
        this.player = player;
    }

    @Override
    public void run() {
        final StringBuilder listsText = new StringBuilder();
        for (final String namespace : commonHandler.getListManager().getNamespaces()) {
            commonHandler.getListManager().getRadarList(namespace)
                    .ifPresent(radarList -> listsText.append("§e").append(namespace).append(" §7(§c")
                            .append(radarList.getRadarListVisibility() == RadarListVisibility.PRIVATE ? Messages.Lists.PRIVATE : Messages.Lists.PUBLIC)
                            .append("§7)").append(", "));
        }

        if (listsText.length() > 0) {
            // players on the list
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.Lists.FOUND)
                    .replace("{lists}", listsText.substring(0, listsText.length() - 2))
                    .build().toChatComponentText());
        } else {
            // list is empty
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.Lists.EMPTY)
                    .build().toChatComponentText());
        }
    }
}
