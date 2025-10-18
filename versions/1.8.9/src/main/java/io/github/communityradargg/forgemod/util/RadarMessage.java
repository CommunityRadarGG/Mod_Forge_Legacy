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
package io.github.communityradargg.forgemod.util;

import net.minecraft.util.ChatComponentText;
import org.jetbrains.annotations.NotNull;

/**
 * A class representing a message that can be shown to a player.
 */
public class RadarMessage {
    private final String message;

    /**
     * Constructs a {@link RadarMessage}.
     *
     * @param message The message.
     * @param includePrefix Whether a prefix should be included in the message.
     */
    private RadarMessage(final @NotNull String message, final boolean includePrefix) {
        this.message = (includePrefix ? Messages.PREFIX : "") + message;
    }

    /**
     * Converts this class instance to a {@link ChatComponentText}.
     *
     * @return Returns the message converted to a {@link ChatComponentText}.
     */
    public @NotNull ChatComponentText toChatComponentText() {
        return new ChatComponentText(message);
    }

    /**
     * A class that serves as a builder for the class {@link RadarMessage}.
     */
    public static class RadarMessageBuilder {
        private String message;
        private boolean includePrefix;

        /**
         * Constructs a {@link RadarMessageBuilder}.
         *
         * @param message The message for the builder.
         */
        public RadarMessageBuilder(final @NotNull String message) {
            this.message = message;
            this.includePrefix = true;
        }

        /**
         * Replaces an old message part with a new one in the message stored in this builder.
         *
         * @param oldMessagePart The old message part.
         * @param newMessagePart The new message part.
         * @return Returns the builder after replacing the text.
         */
        public @NotNull RadarMessageBuilder replace(final @NotNull String oldMessagePart, final @NotNull String newMessagePart) {
            this.message = this.message.replace(oldMessagePart, newMessagePart);
            return this;
        }

        /**
         * Replaces old text with a new one in the text stored in this builder by considering color codes.
         *
         * @param oldMessagePart The old message part.
         * @param newMessagePart The new message part.
         * @return Returns the builder after replacing the text and color codes.
         */
        public @NotNull RadarMessageBuilder replaceWithColorCodes(final @NotNull String oldMessagePart, final @NotNull String newMessagePart) {
            this.message = this.message.replace(oldMessagePart, newMessagePart.replace("&", "§"));
            return this;
        }

        /**
         * Sets the prefix exclude state in the builder.
         *
         * @return Returns the builder after setting the prefix exclude state.
         */
        public @NotNull RadarMessageBuilder excludePrefix() {
            this.includePrefix = false;
            return this;
        }

        /**
         * Builds a {@link RadarMessage} out of the builder.
         *
         * @return Returns the build {@link RadarMessage}.
         */
        public @NotNull RadarMessage build() {
            return new RadarMessage(message, includePrefix);
        }
    }
}
