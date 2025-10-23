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

import io.github.communityradargg.forgemod.list.RadarListEntry;
import io.github.communityradargg.forgemod.util.CommonHandler;
import io.github.communityradargg.forgemod.util.Messages;
import io.github.communityradargg.forgemod.util.PlayerInfo;
import io.github.communityradargg.forgemod.util.RadarMessage;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

/**
 * Holds the logic of the check subcommand.
 */
public class CheckSubcommand implements Subcommand {
    private final CommonHandler commonHandler;
    private final String[] args;

    /**
     * Constructs a {@link CheckSubcommand}.
     *
     * @param commonHandler The common handler.
     * @param args The args.
     */
    public CheckSubcommand(final @NotNull CommonHandler commonHandler, final @NotNull String[] args) {
        this.commonHandler = commonHandler;
        this.args = args;
    }

    @Override
    public void run() {
        if (args.length != 2) {
            // missing arguments
            commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.MISSING_ARGS)
                    .build().getMessage());
            return;
        }

        if (args[1].equalsIgnoreCase("*")) {
            // check all argument
            handleCheckAllSubcommand();
            return;
        }
        handleCheckPlayerSubcommand(args);
    }

    /**
     * Handles the check - player subcommand.
     *
     * @param args The arguments passed to the main command.
     */
    private void handleCheckPlayerSubcommand(final @NotNull String[] args) {
        commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.INPUT_PROCESSING)
                .build().getMessage());
        commonHandler.getUuidByPlayerName(commonHandler, args[1]).thenAccept(checkPlayerOptional -> {
            if (!checkPlayerOptional.isPresent()) {
                // player uuid could not be fetched
                commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.Check.FAILED)
                        .build().getMessage());
                return;
            }

            final Optional<RadarListEntry> entryOptional = commonHandler.getListManager().getRadarListEntry(checkPlayerOptional.get());
            if (!entryOptional.isPresent()) {
                // player uuid is on no list
                commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.Check.FAILED)
                        .build().getMessage());
                return;
            }

            final RadarListEntry entry = entryOptional.get();
            commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.Check.FOUND + "\n" + Messages.Check.CHECK_ENTRY)
                    .replaceWithColorCodes("{prefix}", commonHandler.getListManager().getPrefix(entry.uuid()))
                    .replace("{name}", entry.name())
                    .replace("{cause}", entry.cause())
                    .replace("{entryCreationDate}", commonHandler.formatDateTime(entry.entryCreationDate()))
                    .replace("{entryUpdateDate}", commonHandler.formatDateTime(entry.entryUpdateDate()))
                    .build().getMessage());
        });
    }

    /**
     * Handles the check - all subcommand.
     */
    private void handleCheckAllSubcommand() {
        boolean anyPlayerFound = false;
        for (final PlayerInfo playerInfo : commonHandler.getWorldPlayers()) {
            if (playerInfo.getUuid() == null) {
                continue;
            }

            final Optional<RadarListEntry> listEntryOptional = commonHandler.getListManager()
                    .getRadarListEntry(playerInfo.getUuid());
            if (!listEntryOptional.isPresent()) {
                // player uuid is on no list
                continue;
            }

            if (!anyPlayerFound) {
                commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.Check.EVERYONE)
                        .build().getMessage());
                anyPlayerFound = true;
            }

            final RadarListEntry entry = listEntryOptional.get();
            commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.Check.CHECK_ENTRY)
                    .replaceWithColorCodes("{prefix}", commonHandler.getListManager().getPrefix(entry.uuid()))
                    .replace("{name}", entry.name())
                    .replace("{cause}", entry.cause())
                    .replace("{entryCreationDate}", commonHandler.formatDateTime(entry.entryCreationDate()))
                    .replace("{entryUpdateDate}", commonHandler.formatDateTime(entry.entryUpdateDate()))
                    .build().getMessage());
        }

        if (!anyPlayerFound) {
            commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.Check.NOT_FOUND)
                    .build().getMessage());
        }
    }
}
