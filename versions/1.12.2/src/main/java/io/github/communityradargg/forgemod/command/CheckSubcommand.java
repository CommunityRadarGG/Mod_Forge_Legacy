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

import io.github.communityradargg.forgemod.radarlistmanager.RadarListEntry;
import io.github.communityradargg.forgemod.util.CommonHandler;
import io.github.communityradargg.forgemod.util.Messages;
import io.github.communityradargg.forgemod.util.RadarMessage;
import io.github.communityradargg.forgemod.util.Utils;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Holds the logic of the check subcommand.
 */
public class CheckSubcommand implements Subcommand {
    private final CommonHandler commonHandler;
    private final EntityPlayer player;
    private final String[] args;

    /**
     * Constructs a {@link CheckSubcommand}.
     *
     * @param commonHandler The common handler.
     * @param player The player.
     * @param args The args.
     */
    public CheckSubcommand(final @NotNull CommonHandler commonHandler, final @NotNull EntityPlayer player, final @NotNull String[] args) {
        this.commonHandler = commonHandler;
        this.player = player;
        this.args = args;
    }

    @Override
    public void run() {
        if (args.length != 2) {
            // missing arguments
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.MISSING_ARGS)
                    .build().toChatComponentText());
            return;
        }

        if (args[1].equalsIgnoreCase("*")) {
            // check all argument
            handleCheckAllSubcommand(player);
            return;
        }
        handleCheckPlayerSubcommand(player, args);
    }

    /**
     * Handles the check - player subcommand.
     *
     * @param player The player, which executed the subcommand.
     * @param args The arguments passed to the main command.
     */
    private void handleCheckPlayerSubcommand(final @NotNull EntityPlayer player, final @NotNull String[] args) {
        player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.INPUT_PROCESSING)
                .build().toChatComponentText());
        Utils.getUUID(commonHandler, args[1]).thenAccept(checkPlayerOptional -> {
            if (!checkPlayerOptional.isPresent()) {
                // player uuid could not be fetched
                player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.Check.FAILED)
                        .build().toChatComponentText());
                return;
            }

            final Optional<RadarListEntry> entryOptional = commonHandler.getListManager().getRadarListEntry(checkPlayerOptional.get());
            if (!entryOptional.isPresent()) {
                // player uuid is on no list
                player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.Check.FAILED)
                        .build().toChatComponentText());
                return;
            }

            final RadarListEntry entry = entryOptional.get();
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.Check.FOUND + "\n" + Messages.Check.CHECK_ENTRY)
                    .replaceWithColorCodes("{prefix}", commonHandler.getListManager().getPrefix(entry.uuid()))
                    .replace("{name}", entry.name())
                    .replace("{cause}", entry.cause())
                    .replace("{entryCreationDate}", commonHandler.formatDateTime(entry.entryCreationDate()))
                    .replace("{entryUpdateDate}", commonHandler.formatDateTime(entry.entryUpdateDate()))
                    .build().toChatComponentText());
        });
    }

    /**
     * Handles the check - all subcommand.
     *
     * @param player The player, which executed the subcommand.
     */
    private void handleCheckAllSubcommand(final @NotNull EntityPlayer player) {
        boolean anyPlayerFound = false;
        final NetHandlerPlayClient connection = Minecraft.getMinecraft().getConnection();
        if (connection != null) {
            for (final NetworkPlayerInfo networkPlayer : connection.getPlayerInfoMap()) {
                if (networkPlayer.getGameProfile().getId() == null) {
                    continue;
                }

                final Optional<RadarListEntry> listEntryOptional = commonHandler.getListManager()
                        .getRadarListEntry(networkPlayer.getGameProfile().getId());
                if (!listEntryOptional.isPresent()) {
                    // player uuid is on no list
                    continue;
                }

                if (!anyPlayerFound) {
                    player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.Check.EVERYONE)
                            .build().toChatComponentText());
                    anyPlayerFound = true;
                }

                final RadarListEntry entry = listEntryOptional.get();
                player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.Check.CHECK_ENTRY)
                        .replaceWithColorCodes("{prefix}", commonHandler.getListManager().getPrefix(entry.uuid()))
                        .replace("{name}", entry.name())
                        .replace("{cause}", entry.cause())
                        .replace("{entryCreationDate}", commonHandler.formatDateTime(entry.entryCreationDate()))
                        .replace("{entryUpdateDate}", commonHandler.formatDateTime(entry.entryUpdateDate()))
                        .build().toChatComponentText());
            }
        }

        if (!anyPlayerFound) {
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.Check.NOT_FOUND)
                    .build().toChatComponentText());
        }
    }
}
