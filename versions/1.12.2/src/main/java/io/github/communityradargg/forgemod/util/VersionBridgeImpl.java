package io.github.communityradargg.forgemod.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.UUID;

public class VersionBridgeImpl implements VersionBridge {
    private static final Logger LOGGER = LogManager.getLogger(VersionBridgeImpl.class);

    @Override
    public @NotNull String getVersion() {
        final ModContainer modContainer = Loader.instance().getIndexedModList().get(CommonHandler.MOD_ID);
        if (modContainer == null) {
            return "UNKNOWN";
        }

        return modContainer.getVersion();
    }

    @Override
    public void addMessageToChat(final @NotNull String message) {
        if (Minecraft.getMinecraft().player == null) {
            LOGGER.warn("Could not add message to chat. Player is null. The message is following: {}", message);
            return;
        }

        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(message));
    }

    @Override
    public boolean isNotInWorld() {
        return Minecraft.getMinecraft().world == null;
    }

    @Override
    public Optional<UUID> getPlayerUuidByNameFromWorld(final @NotNull String playerName) {
        final NetHandlerPlayClient connection = Minecraft.getMinecraft().getConnection();
        if (connection == null) {
            return Optional.empty();
        }

        for (final NetworkPlayerInfo networkPlayerInfo : connection.getPlayerInfoMap()) {
            if (networkPlayerInfo.getGameProfile().getName().equalsIgnoreCase(playerName)) {
                return Optional.of(networkPlayerInfo.getGameProfile().getId());
            }
        }
        return Optional.empty();
    }
}
