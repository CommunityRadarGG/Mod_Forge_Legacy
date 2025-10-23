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
import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Holds the logic of the list subcommand.
 */
public class ListSubcommand implements Subcommand {
    private final CommonHandler commonHandler;
    private final String[] args;

    /**
     * Constructs a {@link ListSubcommand}.
     *
     * @param commonHandler The common handler.
     * @param args The args.
     */
    public ListSubcommand(final @NotNull CommonHandler commonHandler, final @NotNull String[] args) {
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
                handleListAddSubcommand(args);
                break;
            case "PREFIX":
                handleListPrefixSubcommand(args);
                break;
            case "DELETE":
                handleListDeleteSubcommand(args);
                break;
            case "SHOW":
                handleListShowSubcommand(args);
                break;
            default:
                new HelpSubcommand(commonHandler).run();
                break;
        }
    }

    /**
     * Handles the list - add subcommand.
     *
     * @param args The arguments passed to the main command.
     */
    private void handleListAddSubcommand(final @NotNull String[] args) {
        if (args.length != 4) {
            // missing arguments
            commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.MISSING_ARGS)
                    .build().getMessage());
            return;
        }

        if (commonHandler.getListManager().getRadarList(args[2]).isPresent()) {
            // list already existing
            commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.List.CREATE_FAILED)
                    .build().getMessage());
            return;
        }

        if (!commonHandler.getListManager().registerPrivateList(args[2], args[3])) {
            // list could not be registered
            commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.List.CREATE_FAILED)
                    .build().getMessage());
            return;
        }

        commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.List.CREATE_SUCCESS)
                .build().getMessage());
    }

    /**
     * Handles the list - delete subcommand.
     *
     * @param args The arguments passed to the main command.
     */
    private void handleListDeleteSubcommand(final @NotNull String[] args) {
        if (args.length != 3) {
            // missing arguments
            commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.MISSING_ARGS)
                    .build().getMessage());
            return;
        }

        final ListManager listManager = commonHandler.getListManager();
        final Set<String> oldPrefixes = listManager.getExistingPrefixes();
        final Set<UUID> oldUuids = listManager.getRadarList(args[2])
                .map(radarList -> radarList.getPlayerMap().keySet())
                .orElse(Collections.emptySet());

        if (!listManager.unregisterList(args[2])) {
            // list is not existing, list is not private, file cannot be deleted
            commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.List.DELETE_FAILED)
                    .build().getMessage());
            return;
        }

        oldUuids.forEach(uuid -> commonHandler.updatePlayerByUuid(uuid, oldPrefixes));
        commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.List.DELETE_SUCCESS)
                .build().getMessage());
    }

    /**
     * Handles the list - show subcommand.
     *
     * @param args The arguments passed to the main command.
     */
    private void handleListShowSubcommand(final @NotNull String[] args) {
        if (args.length != 3) {
            // missing arguments
            commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.MISSING_ARGS)
                    .build().getMessage());
            return;
        }

        final Optional<RadarList> listOptional = commonHandler.getListManager().getRadarList(args[2]);
        if (!listOptional.isPresent()) {
            // list is not existing
            commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.List.SHOW_FAILED)
                    .build().getMessage());
            return;
        }

        final RadarList list = listOptional.get();
        if (list.getPlayerMap().isEmpty()) {
            // list is empty
            commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.List.SHOW_EMPTY)
                    .build().getMessage());
            return;
        }

        final StringBuilder players = new StringBuilder();
        list.getPlayerMap().values().forEach(value -> players.append(value.name()).append(", "));
        commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.List.SHOW_SUCCESS)
                .replace("{list}", list.getNamespace())
                .replaceWithColorCodes("{prefix}", listOptional.get().getPrefix())
                .replace("{players}", players.substring(0, players.length() - 2))
                .build().getMessage());
    }

    /**
     * Handles the list - prefix subcommand.
     *
     * @param args The arguments passed to the main command.
     */
    private void handleListPrefixSubcommand(final @NotNull String[] args) {
        if (args.length != 4) {
            // missing arguments
            commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.MISSING_ARGS)
                    .build().getMessage());
            return;
        }

        final ListManager listManager = commonHandler.getListManager();
        final Optional<RadarList> listOptional = listManager.getRadarList(args[2]);
        if (!listOptional.isPresent()) {
            // list is not existing
            commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.List.PREFIX_FAILED)
                    .build().getMessage());
            return;
        }

        final RadarList list = listOptional.get();
        final Set<String> oldPrefixes = listManager.getExistingPrefixes();
        list.setPrefix(args[3]);
        list.saveList();
        list.getPlayerMap().keySet().forEach(uuid -> commonHandler.updatePlayerByUuid(uuid, oldPrefixes));

        commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.List.PREFIX_SUCCESS)
                .replaceWithColorCodes("{prefix}", args[3])
                .build().getMessage());
    }
}
