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
