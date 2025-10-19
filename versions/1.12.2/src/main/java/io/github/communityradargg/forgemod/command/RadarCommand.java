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
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The class containing all logic for the radar command.
 */
public class RadarCommand extends CommandBase {
    private final RootRadarCommand rootRadarCommand;

    /**
     * Constructs a {@link RadarCommand}.
     *
     * @param commonHandler The common handler.
     */
    public RadarCommand(final @NotNull CommonHandler commonHandler) {
        rootRadarCommand = new RootRadarCommand(commonHandler);
    }

    @Override
    public @NotNull String getName() {
        return RootRadarCommand.COMMAND_NAME;
    }

    @Override
    public @NotNull String getUsage(final @NotNull ICommandSender sender) {
        return "/" + RootRadarCommand.COMMAND_NAME;
    }

    @Override
    public @NotNull List<String> getAliases() {
        return RootRadarCommand.COMMAND_ALIASES;
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
            rootRadarCommand.execute(args);
    }
}
