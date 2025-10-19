package io.github.communityradargg.forgemod.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
        if (Minecraft.getMinecraft().thePlayer == null) {
            LOGGER.warn("Could not add message to chat. Player is null. The message is following: {}", message);
            return;
        }

        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
    }

    @Override
    public boolean isNotInWorld() {
        return Minecraft.getMinecraft().theWorld == null;
    }

    @Override
    public @NotNull List<@NotNull PlayerInfo> getWorldPlayers() {
        final NetHandlerPlayClient connection = Minecraft.getMinecraft().getNetHandler();
        if (connection == null) {
            return Collections.emptyList();
        }

        return connection.getPlayerInfoMap().stream()
                .map(networkPlayerInfo -> {
                    final GameProfile gameProfile = networkPlayerInfo.getGameProfile();
                    return new PlayerInfo(gameProfile.getId(), gameProfile.getName());
                })
                .collect(Collectors.toList());
    }

    @Override
    public void updatePlayerByUuid(final @NotNull CommonHandler commonHandler, final @NotNull UUID uuid, final @NotNull Set<@NotNull String> oldPrefixes) {
        Utils.updatePlayerByUuid(commonHandler, uuid, oldPrefixes);
    }

    @Override
    public void updatePrefixes(final @NotNull CommonHandler commonHandler, final @NotNull Set<String> oldPrefixes) {
        Utils.updatePrefixes(commonHandler, oldPrefixes);
    }

    @Override
    public boolean isPlayerListKeyPressed() {
        return Minecraft.getMinecraft().gameSettings.keyBindPlayerList.isPressed();
    }

    @Override
    public @NotNull String wrapAndUnformatText(final @NotNull String text) {
        return new ChatComponentText(text).getUnformattedText();
    }
}
