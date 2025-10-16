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
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The class containing all logic for the radar command.
 */
public class RadarCommand extends CommandBase {
    private final CommunityRadarMod communityRadarMod;

    /**
     * Constructs a {@link RadarCommand}.
     *
     * @param communityRadarMod The mod main class instance.
     */
    public RadarCommand(final CommunityRadarMod communityRadarMod) {
        this.communityRadarMod = communityRadarMod;
    }

    @Override
    public @NotNull String getName() {
        return "radar";
    }

    @Override
    public @NotNull String getUsage(final @NotNull ICommandSender sender) {
        return "/radar";
    }

    @Override
    public @NotNull List<String> getAliases() {
        return Arrays.asList("communityradar", "scammer", "trustedmm", "mm");
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean checkPermission(final @NotNull MinecraftServer server, final @NotNull ICommandSender sender) {
        return sender instanceof EntityPlayer;
    }

    @Override
    public void execute(final @NotNull MinecraftServer server, final @NotNull ICommandSender sender, final String @NotNull [] args) {
        if (!(sender instanceof EntityPlayer)) {
            sender.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.NOT_PLAYER)
                    .build()
                    .toChatComponentText());
            return;
        }

        final EntityPlayer player = (EntityPlayer) sender;
        Subcommand subcommand = null;
        if (args.length == 0) {
            subcommand = new HelpSubcommand(communityRadarMod, player);
        }

        if (subcommand == null) {
            switch (args[0].toUpperCase(Locale.ENGLISH)) {
                case "CHECK":
                    subcommand = new CheckSubcommand(communityRadarMod, player, args);
                    break;
                case "LIST":
                    subcommand = new ListSubcommand(communityRadarMod, player, args);
                    break;
                case "PLAYER":
                    subcommand = new PlayerSubcommand(communityRadarMod, player, args);
                    break;
                case "LISTS":
                    subcommand = new ListsSubcommand(communityRadarMod, player);
                    break;
                default:
                    subcommand = new HelpSubcommand(communityRadarMod, player);
                    break;
            }
        }

        subcommand.run();
    }
}
