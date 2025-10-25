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

import io.github.communityradargg.forgemod.util.CommonHandler;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The root radar command.
 */
public class RootRadarCommand {
    public static final String COMMAND_NAME = "radar";
    public static final List<String> COMMAND_ALIASES = Arrays.asList("communityradar", "scammer", "trustedmm", "mm");
    private final CommonHandler commonHandler;

    /**
     * Constructs as {@link RootRadarCommand}.
     *
     * @param commonHandler The common handler.
     */
    public RootRadarCommand(final @NotNull CommonHandler commonHandler) {
        this.commonHandler = commonHandler;
    }

    /**
     * Executes the root command with the given arguments.
     *
     * @param args The arguments.
     */
    public void execute(final String[] args) {
        Subcommand subcommand = null;
        if (args.length == 0) {
            subcommand = new HelpSubcommand(commonHandler);
        }

        if (subcommand == null) {
            switch (args[0].toUpperCase(Locale.ENGLISH)) {
                case "CHECK":
                    subcommand = new CheckSubcommand(commonHandler, args);
                    break;
                case "LIST":
                    subcommand = new ListSubcommand(commonHandler, args);
                    break;
                case "PLAYER":
                    subcommand = new PlayerSubcommand(commonHandler, args);
                    break;
                case "LISTS":
                    subcommand = new ListsSubcommand(commonHandler);
                    break;
                default:
                    subcommand = new HelpSubcommand(commonHandler);
                    break;
            }
        }

        subcommand.run();
    }
}
