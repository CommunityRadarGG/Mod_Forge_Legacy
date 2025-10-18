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
import io.github.communityradargg.forgemod.util.Messages;
import io.github.communityradargg.forgemod.util.RadarMessage;
import org.jetbrains.annotations.NotNull;

/**
 * Holds the logic of the help subcommand.
 */
public class HelpSubcommand implements Subcommand {
    private final CommonHandler commonHandler;

    /**
     * Constructs a {@link HelpSubcommand}.
     *
     * @param commonHandler The common handler.
     */
    public HelpSubcommand(final @NotNull CommonHandler commonHandler) {
        this.commonHandler = commonHandler;
    }

    @Override
    public void run() {
        commonHandler.addMessageToChat(new RadarMessage.RadarMessageBuilder(Messages.HELP)
                .replace("{code_version}", commonHandler.getVersion())
                .excludePrefix()
                .build().getMessage());
    }
}
