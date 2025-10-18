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
import org.jetbrains.annotations.NotNull;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * Holds the logic of the player subcommand.
 */
public class PlayerSubcommand implements Subcommand {
    private final CommonHandler commonHandler;
    private final String[] args;

    /**
     * Constructs a {@link PlayerSubcommand}.
     *
     * @param commonHandler The common handler.
     * @param args The args.
     */
    public PlayerSubcommand(final @NotNull CommonHandler commonHandler, final @NotNull String[] args) {
        this.commonHandler = commonHandler;
        this.args = args;
    }

    @Override
    public void run() {
        if (args.length < 2) {
            // missing arguments
            commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.MISSING_ARGS)
                    .build().getMessage());
            return;
        }

        switch (args[1].toUpperCase(Locale.ENGLISH)) {
            case "ADD":
                handlePlayerAddSubcommand(args);
                break;
            case "REMOVE":
                handlePlayerRemoveSubcommand(args);
                break;
            default:
                new HelpSubcommand(commonHandler).run();
                break;
        }
    }

    /**
     * Handles the player - add subcommand.
     *
     * @param args The arguments passed to the main command.
     */
    private void handlePlayerAddSubcommand(final @NotNull String[] args) {
        if (args.length < 5) {
            // missing arguments
            commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.MISSING_ARGS)
                    .build().getMessage());
            return;
        }

        final ListManager listManager = commonHandler.getListManager();
        final Optional<RadarList> listOptional = listManager.getRadarList(args[2]);
        if (!listOptional.isPresent()) {
            // list not existing
            commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.Player.ADD_FAILED)
                    .build().getMessage());
            return;
        }

        commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.INPUT_PROCESSING)
                .build().getMessage());
        commonHandler.getUuidByPlayerName(commonHandler, args[3]).thenAccept(uuidOptional -> {
            if (!uuidOptional.isPresent()) {
                // player uuid could not be fetched
                commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(args[3].startsWith("!") ? Messages.Player.NAME_INVALID_BEDROCK : Messages.Player.NAME_INVALID)
                        .build().getMessage());
                return;
            }

            final UUID uuid = uuidOptional.get();
            if (listOptional.get().isInList(uuid)) {
                // player already on list
                commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.Player.ADD_IN_LIST)
                        .build().getMessage());
                return;
            }

            final StringBuilder notes = new StringBuilder();
            for (int i = 4; i < args.length; i++) {
                notes.append(args[i]).append(" ");
            }

            if (!commonHandler.getListManager().addRadarListEntry(args[2], uuid, args[3], notes.substring(0, notes.length() - 1))) {
                // list is not private
                commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.Player.ADD_FAILED)
                        .build().getMessage());
                return;
            }

            commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.Player.ADD_SUCCESS)
                    .build().getMessage());
            Utils.updatePlayerByUuid(commonHandler, uuid, listManager.getExistingPrefixes());
        });
    }

    /**
     * Handles the player - remove subcommand.
     *
     * @param args The arguments passed to the main command.
     */
    private void handlePlayerRemoveSubcommand(final @NotNull String[] args) {
        if (args.length != 4) {
            // missing arguments
            commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.MISSING_ARGS)
                    .build().getMessage());
            return;
        }

        final ListManager listManager =  commonHandler.getListManager();
        final Optional<RadarList> listOptional = listManager.getRadarList(args[2]);
        if (!listOptional.isPresent()) {
            // list is not existing
            commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.Player.REMOVE_FAILED)
                    .build().getMessage());
            return;
        }

        commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.INPUT_PROCESSING)
                .build().getMessage());
        final RadarList list = listOptional.get();
        commonHandler.getUuidByPlayerName(commonHandler, args[3]).thenAccept(uuidOptional -> {
            if (!uuidOptional.isPresent()) {
                // player uuid could not be fetched
                commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(args[3].startsWith("!") ? Messages.Player.NAME_INVALID_BEDROCK : Messages.Player.NAME_INVALID)
                        .build().getMessage());
                return;
            }

            final UUID uuid = uuidOptional.get();
            if (!list.isInList(uuid)) {
                // player uuid not on list
                commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.Player.REMOVE_NOT_IN_LIST)
                        .build().getMessage());
                return;
            }

            list.getPlayerMap().remove(uuid);
            Utils.updatePlayerByUuid(commonHandler, uuid, listManager.getExistingPrefixes());
            commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.Player.REMOVE_SUCCESS)
                    .build().getMessage());
        });
    }
}
