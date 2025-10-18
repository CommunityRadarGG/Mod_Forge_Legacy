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

import io.github.communityradargg.forgemod.list.ListManager;
import io.github.communityradargg.forgemod.list.RadarList;
import io.github.communityradargg.forgemod.util.CommonHandler;
import io.github.communityradargg.forgemod.util.Messages;
import io.github.communityradargg.forgemod.util.RadarMessage;
import io.github.communityradargg.forgemod.util.Utils;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Holds the logic of the list subcommand.
 */
public class ListSubcommand implements Subcommand {
    private final CommonHandler commonHandler;
    private final EntityPlayer player;
    private final String[] args;

    /**
     * Constructs a {@link ListSubcommand}.
     *
     * @param commonHandler The common handler.
     * @param player The player.
     * @param args The args.
     */
    public ListSubcommand(final @NotNull CommonHandler commonHandler, final @NotNull EntityPlayer player, final @NotNull String[] args) {
        this.commonHandler = commonHandler;
        this.player = player;
        this.args = args;
    }

    @Override
    public void run() {
        if (args.length < 2) {
            // missing arguments
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.MISSING_ARGS)
                    .build().toChatComponentText());
            return;
        }

        switch (args[1].toUpperCase(Locale.ENGLISH)) {
            case "ADD":
                handleListAddSubcommand(player, args);
                break;
            case "PREFIX":
                handleListPrefixSubcommand(player, args);
                break;
            case "DELETE":
                handleListDeleteSubcommand(player, args);
                break;
            case "SHOW":
                handleListShowSubcommand(player, args);
                break;
            default:
                new HelpSubcommand(commonHandler, player).run();
                break;
        }
    }

    /**
     * Handles the list - add subcommand.
     *
     * @param player The player, which executed the subcommand.
     * @param args The arguments passed to the main command.
     */
    private void handleListAddSubcommand(final @NotNull EntityPlayer player, final @NotNull String[] args) {
        if (args.length != 4) {
            // missing arguments
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.MISSING_ARGS)
                    .build().toChatComponentText());
            return;
        }

        if (commonHandler.getListManager().getRadarList(args[2]).isPresent()) {
            // list already existing
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.List.CREATE_FAILED)
                    .build().toChatComponentText());
            return;
        }

        if (!commonHandler.getListManager().registerPrivateList(args[2], args[3])) {
            // list could not be registered
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.List.CREATE_FAILED)
                    .build().toChatComponentText());
            return;
        }

        player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.List.CREATE_SUCCESS)
                .build().toChatComponentText());
    }

    /**
     * Handles the list - delete subcommand.
     *
     * @param player The player, which executed the subcommand.
     * @param args The arguments passed to the main command.
     */
    private void handleListDeleteSubcommand(final @NotNull EntityPlayer player, final @NotNull String[] args) {
        if (args.length != 3) {
            // missing arguments
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.MISSING_ARGS)
                    .build().toChatComponentText());
            return;
        }

        final ListManager listManager = commonHandler.getListManager();
        final Set<String> oldPrefixes = listManager.getExistingPrefixes();
        final Set<UUID> oldUuids = listManager.getRadarList(args[2])
                .map(radarList -> radarList.getPlayerMap().keySet())
                .orElse(Collections.emptySet());

        if (!listManager.unregisterList(args[2])) {
            // list is not existing, list is not private, file cannot be deleted
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.List.DELETE_FAILED)
                    .build().toChatComponentText());
            return;
        }

        oldUuids.forEach(uuid -> Utils.updatePlayerByUuid(commonHandler, uuid, oldPrefixes));
        player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.List.DELETE_SUCCESS)
                .build().toChatComponentText());
    }

    /**
     * Handles the list - show subcommand.
     *
     * @param player The player, which executed the subcommand.
     * @param args The arguments passed to the main command.
     */
    private void handleListShowSubcommand(final @NotNull EntityPlayer player, final @NotNull String[] args) {
        if (args.length != 3) {
            // missing arguments
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.MISSING_ARGS)
                    .build().toChatComponentText());
            return;
        }

        final Optional<RadarList> listOptional = commonHandler.getListManager().getRadarList(args[2]);
        if (!listOptional.isPresent()) {
            // list is not existing
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.List.SHOW_FAILED)
                    .build().toChatComponentText());
            return;
        }

        final RadarList list = listOptional.get();
        if (list.getPlayerMap().isEmpty()) {
            // list is empty
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.List.SHOW_EMPTY)
                    .build().toChatComponentText());
            return;
        }

        final StringBuilder players = new StringBuilder();
        list.getPlayerMap().values().forEach(value -> players.append(value.name()).append(", "));
        player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.List.SHOW_SUCCESS)
                .replace("{list}", list.getNamespace())
                .replaceWithColorCodes("{prefix}", listOptional.get().getPrefix())
                .replace("{players}", players.substring(0, players.length() - 2))
                .build().toChatComponentText());
    }

    /**
     * Handles the list - prefix subcommand.
     *
     * @param player The player, which executed the subcommand.
     * @param args The arguments passed to the main command.
     */
    private void handleListPrefixSubcommand(final @NotNull EntityPlayer player, final @NotNull String[] args) {
        if (args.length != 4) {
            // missing arguments
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.MISSING_ARGS)
                    .build().toChatComponentText());
            return;
        }

        final ListManager listManager = commonHandler.getListManager();
        final Optional<RadarList> listOptional = listManager.getRadarList(args[2]);
        if (!listOptional.isPresent()) {
            // list is not existing
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.List.PREFIX_FAILED)
                    .build().toChatComponentText());
            return;
        }

        final RadarList list = listOptional.get();
        final Set<String> oldPrefixes = listManager.getExistingPrefixes();
        list.setPrefix(args[3]);
        list.saveList();
        list.getPlayerMap().keySet().forEach(uuid -> Utils.updatePlayerByUuid(commonHandler, uuid, oldPrefixes));

        player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.List.PREFIX_SUCCESS)
                .replaceWithColorCodes("{prefix}", args[3])
                .build().toChatComponentText());
    }
}
