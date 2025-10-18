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
package io.github.communityradargg.forgemod.list;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import io.github.communityradargg.forgemod.radarlistmanager.RadarListEntry;
import io.github.communityradargg.forgemod.radarlistmanager.RadarListVisibility;
import io.github.communityradargg.forgemod.radarlistmanager.adapters.GsonLocalDateTimeAdapter;
import io.github.communityradargg.forgemod.radarlistmanager.adapters.GsonRadarListPlayerMapAdapter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import io.github.communityradargg.forgemod.util.CommonHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A class containing the methods to manage lists.
 */
public class ListManager {
    private static final Logger LOGGER = LogManager.getLogger(ListManager.class);
    static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeAdapter())
            .registerTypeAdapter(Map.class, new GsonRadarListPlayerMapAdapter())
            .create();
    private final CommonHandler commonHandler;
    private final List<RadarList> lists;
    private final String directoryPath;

    /**
     * Constructs a {@link ListManager}
     *
     * @param commonHandler The common handler.
     */
    public ListManager(final @NotNull CommonHandler commonHandler) {
        this.lists = new ArrayList<>();
        this.commonHandler = commonHandler;

        directoryPath = createDirectoryPath();
    }

    // TODO Look if this can be optimized in Java 8
    /**
     * Creates the list directory path.
     *
     * @return Returns the list directory path.
     */
    private String createDirectoryPath() {
        final File directoryFilePath = Paths.get(new File("")
                        .getAbsolutePath(), "communityradar", "lists")
                .toFile();

        if (!directoryFilePath.exists() && !directoryFilePath.mkdirs()) {
            LOGGER.error("Could not create directory: {}", directoryPath);
        }

        return directoryFilePath.getAbsolutePath() + "/";
    }

    /**
     * Checks if a given uuid is in a list.
     *
     * @param uuid The uuid to check.
     * @return Returns, whether the uuid is in a list.
     */
    public boolean isInList(final @NotNull UUID uuid) {
        return lists.stream()
                .anyMatch(list -> list.isInList(uuid));
    }

    /**
     * Gets the first prefix linked to a given uuid.
     *
     * @param uuid The uuid to get the prefix for.
     * @return Returns the prefix.
     */
    public @NotNull String getPrefix(final @NotNull UUID uuid) {
        return lists.stream()
                .filter(list -> list.isInList(uuid))
                .map(RadarList::getPrefix)
                .findFirst()
                .orElse("");
    }

    /**
     * Gets all existing namespaces.
     *
     * @return Returns a set with all existing namespaces.
     */
    public @NotNull Set<String> getNamespaces() {
        return lists.stream()
                .map(RadarList::getNamespace)
                .collect(Collectors.toSet());
    }

    /**
     * Gets an optional with a {@link RadarListEntry} by a given uuid.
     *
     * @param uuid The uuid to get the entry for.
     * @return Returns an optional with the found entry.
     */
    public @NotNull Optional<RadarListEntry> getRadarListEntry(final @NotNull UUID uuid) {
        return lists.stream()
                .filter(list -> list.isInList(uuid))
                .findFirst()
                .flatMap(list -> list.getRadarListEntry(uuid));
    }

    /**
     * Gets an optional with a {@link RadarList} by a given namespace.
     *
     * @param namespace The namespace to get the list for.
     * @return Returns an optional with the found list.
     */
    public @NotNull Optional<RadarList> getRadarList(final @NotNull String namespace) {
        return lists.stream()
                .filter(list -> list.getNamespace().equalsIgnoreCase(namespace))
                .findFirst();
    }

    /**
     * Adds a player entry to a list.
     *
     * @param namespace The namespace of the list.
     * @param uuid The player uuid for the entry.
     * @param name The player name for the entry.
     * @param cause The cause for the entry.
     * @return Returns, whether the entry was successfully added.
     */
    public boolean addRadarListEntry(final @NotNull String namespace, final @NotNull UUID uuid, final @NotNull String name, final @NotNull String cause) {
        if (getRadarListEntry(uuid).isPresent()) {
            return false;
        }

        final Optional<RadarList> listOptional = lists.stream()
                .filter(list -> list.getNamespace().equalsIgnoreCase(namespace))
                .findFirst();

        if (listOptional.isPresent()) {
            final RadarList list = listOptional.get();

            if (list.getRadarListVisibility() == RadarListVisibility.PRIVATE) {
                list.addRadarListEntry(new RadarListEntry(uuid, name, cause, LocalDateTime.now()));
                return true;
            }
        }
        return false;
    }

    /**
     * Saves a radar list to disk if it is a private one.
     *
     * @param list The list to save.
     */
    public void saveRadarList(final @NotNull RadarList list) {
        if (list.getRadarListVisibility() != RadarListVisibility.PRIVATE) {
            return;
        }

        try (final FileWriter writer = new FileWriter(directoryPath + list.getNamespace() + ".json")) {
            writer.write(GSON.toJson(list));
        } catch (final IOException e) {
            LOGGER.error("Could not save list", e);
        }
    }

    /**
     * Registers a private list.
     *
     * @param namespace The namespace of the list.
     * @param prefix The prefix of the list.
     * @return Returns, whether the list was successfully registered.
     */
    public boolean registerPrivateList(final @NotNull String namespace, final @NotNull String prefix) {
        final boolean namespaceExists = getNamespaces().stream()
                .anyMatch(namespace::equalsIgnoreCase);
        if (namespaceExists) {
            return false;
        }

        lists.add(new RadarList(commonHandler, namespace, prefix, directoryPath + namespace + ".json", RadarListVisibility.PRIVATE));

        final Optional<RadarList> listOptional = getRadarList(namespace);
        if (!listOptional.isPresent()) {
            return false;
        }

        saveRadarList(listOptional.get());
        return true;
    }

    /**
     * Registers a public list.
     *
     * @param namespace The namespace of the list.
     * @param prefix The prefix of the list.
     * @return Returns, whether the list was successfully registered.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted") // better understanding of code logic
    public boolean registerPublicList(final @NotNull String namespace, final @NotNull String prefix, final @NotNull String url) {
        final boolean namespaceExists = getNamespaces().stream()
                .anyMatch(namespace::equalsIgnoreCase);
        if (namespaceExists) {
            return false;
        }

        lists.add(new RadarList(commonHandler, namespace, prefix, url, RadarListVisibility.PUBLIC));
        return true;
    }

    /**
     * Adds a radar list if it is not null.
     *
     * @param list The nullable radar list.
     */
    private void addRadarList(final @Nullable RadarList list) {
        if (list != null) {
            lists.add(list);
        }
    }

    /**
     * Unregisters a list by its namespace.
     *
     * @param namespace The namespace of the list.
     * @return Returns, whether the list was successfully unregistered.
     */
    public boolean unregisterList(final @NotNull String namespace) {
        final Optional<RadarList> listOptional = getRadarList(namespace);
        if (!listOptional.isPresent()) {
            return false;
        }

        final RadarList list = listOptional.get();
        if (list.getRadarListVisibility() == RadarListVisibility.PUBLIC) {
            return false;
        }

        final File file = new File(list.getUrl());
        if (file.exists() && !file.delete()) {
            return false;
        }

        lists.remove(list);
        return true;
    }

    /**
     * Loads the private lists from disk.
     */
    public void loadPrivateLists() {
        getJsonUrls(directoryPath)
                .forEach(jsonUrl -> loadRadarListFromFile(jsonUrl)
                        .ifPresent(this::addRadarList));
    }

    /**
     * Loads a radar list from a file.
     *
     * @param filePath The path to the file.
     * @return Returns an optional with the loaded radar list.
     */
    private @NotNull Optional<RadarList> loadRadarListFromFile(final @NotNull String filePath) {
        try (final FileReader reader = new FileReader(filePath)) {
            final RadarList list = GSON.fromJson(reader, new TypeToken<RadarList>() {}.getType());
            list.setUrl(filePath);
            if (list.validateList()) {
                return Optional.of(list);
            }
        } catch (final IOException | IllegalStateException | JsonIOException | JsonSyntaxException e) {
            LOGGER.error("Could not load list from file", e);
        }
        return Optional.empty();
    }

    /**
     * Gets the json urls for the directory paths.
     *
     * @param directoryPath The directory path.
     * @return Returns a set with all json urls.
     */
    private @NotNull Set<String> getJsonUrls(final @NotNull String directoryPath) {
        try (final Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(string -> string.endsWith(".json"))
                    .collect(Collectors.toSet());
        } catch (final IOException e) {
            LOGGER.error("Could not get json urls", e);
        }
        return new HashSet<>();
    }

    /**
     * Gets all existing prefixes.
     *
     * @return Returns a set with all existing prefixes.
     */
    public @NotNull Set<String> getExistingPrefixes() {
        return lists.stream()
                .map(RadarList::getPrefix)
                .collect(Collectors.toSet());
    }
}
