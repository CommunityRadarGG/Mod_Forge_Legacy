package io.github.communityradargg.forgemod.util;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

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
}
