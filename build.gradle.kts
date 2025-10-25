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
plugins {
    id("java")
    alias(libs.plugins.spotless)
    alias(libs.plugins.ggEssentialLoom) apply false
}

val groupTextProvider = providers.gradleProperty("maven_group")
val versionTextProvider = providers.gradleProperty("mod_version")
val modIdProvider = providers.gradleProperty("mod_id")

allprojects {
    group = groupTextProvider.get()
    version = versionTextProvider.get()

    repositories {
        mavenCentral()
    }

    plugins.withId("com.diffplug.spotless") {
        spotless {
            java {
                licenseHeaderFile(rootProject.file("HEADER"))
                endWithNewline()
                trimTrailingWhitespace()
                removeUnusedImports()
                removeWildcardImports()
            }

            kotlinGradle {
                licenseHeaderFile(rootProject.file("HEADER"), "(plugins|import|buildscript|pluginManagement|base)")
                endWithNewline()
                trimTrailingWhitespace()
            }

            kotlin {
                licenseHeaderFile(rootProject.file("HEADER"))
                endWithNewline()
                trimTrailingWhitespace()
            }
        }
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)

    val computedVersion = if (path.startsWith(":versions:")) {
        "${versionTextProvider.get()}+$name"
    } else {
        versionTextProvider.get()
    }

    version = computedVersion

    extra["versionText"] = computedVersion
    extra["modIdText"] = modIdProvider.get()

    base {
        archivesName.set(modIdProvider.get())
    }

    project(":versions").childProjects.values.forEach { versionProject ->
        versionProject.apply(plugin = rootProject.libs.plugins.ggEssentialLoom.get().pluginId)
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(8))
        }
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = Charsets.UTF_8.name()
        }

        withType<Jar> {
            from(rootProject.layout.projectDirectory) {
                include("LICENSE", "NOTICE")
            }

            manifest {
                attributes(
                    "Specification-Title" to rootProject.name,
                    "Specification-Version" to versionTextProvider.get(),
                    "Implementation-Title" to rootProject.name,
                    "Implementation-Version" to versionTextProvider.get()
                )
            }

            plugins.withId(rootProject.libs.plugins.ggEssentialLoom.get().pluginId) {
                val remapJarTask = named("remapJar")

                register<Copy>("copyRemappedJars") {
                    dependsOn(remapJarTask)
                    from(remapJarTask.map { it.outputs.files })
                    into(rootProject.layout.buildDirectory.dir("libs"))
                }

                named("build") {
                    finalizedBy("copyRemappedJars")
                }
            }
        }
    }
}

tasks {
    named<Jar>("jar").configure {
        enabled = false
    }
}
