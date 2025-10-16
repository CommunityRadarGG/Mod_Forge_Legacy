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

import io.github.communityradargg.forgemod.CommunityRadarMod;
import io.github.communityradargg.forgemod.util.Messages;
import io.github.communityradargg.forgemod.util.RadarMessage;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Holds the logic of the help subcommand.
 */
public class HelpSubcommand implements Subcommand {
    private final CommunityRadarMod communityRadarMod;
    private final EntityPlayer player;

    /**
     * Constructs a {@link HelpSubcommand}.
     *
     * @param communityRadarMod The mod main class instance.
     * @param player The player.
     */
    public HelpSubcommand(final @NotNull CommunityRadarMod communityRadarMod, final @NotNull EntityPlayer player) {
        this.communityRadarMod = communityRadarMod;
        this.player = player;
    }

    @Override
    public void run() {
        player.addChatComponentMessage(new RadarMessage.RadarMessageBuilder(Messages.HELP)
                .replace("{code_version}", communityRadarMod.getVersion())
                .excludePrefix()
                .build().toChatComponentText());
    }
}
