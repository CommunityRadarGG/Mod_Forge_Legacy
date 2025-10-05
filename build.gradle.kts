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
import dev.architectury.pack200.java.Pack200Adapter

plugins {
    id("java")
    alias(libs.plugins.ggEssentialLoom)
    alias(libs.plugins.architecturyPack200)
    alias(libs.plugins.spotless)
}

version = project.extra.get("mod_version") as String
group = project.extra.get("maven_group") as String

base {
    archivesName.set(project.extra.get("mod_id") as String)
}

repositories {
    mavenCentral()
}

dependencies {
    minecraft(libs.minecraft)
    mappings(libs.mcpMappings)
    forge(libs.forge)
}

loom {
    forge {
        pack200Provider.set(Pack200Adapter())
        // accessTransformer("src/main/resources/META-INF/${project.mod_id}_at.cfg")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

tasks {
    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
    }

    register<Copy>("includeLicenses") {
        from(project.projectDir) {
            include("LICENSE", "NOTICE")
        }

        into(sourceSets["main"].output.resourcesDir!!)
    }

    withType<ProcessResources> {
        dependsOn("includeLicenses")

        // https://github.com/gradle/gradle/issues/861
        outputs.upToDateWhen { false }

        filteringCharset = Charsets.UTF_8.name()

        filesMatching("mcmod.info") {
            expand(
                mapOf(
                    "version" to project.version,
                    "mc_version" to libs.versions.minecraft.get(),
                    "mod_id" to project.extra.get("mod_id") as String
                )
            )
        }
    }
}

sourceSets {
    main {
        output.setResourcesDir(java.classesDirectory)
    }
}

spotless {
    java {
        licenseHeaderFile(rootProject.file("HEADER"))
        endWithNewline()
        trimTrailingWhitespace()
        removeUnusedImports()
        removeWildcardImports()
    }

    kotlin {
        licenseHeaderFile(rootProject.file("HEADER"))
        endWithNewline()
        trimTrailingWhitespace()
    }
}

// source replacement
val sourceReplacementOutput = layout.buildDirectory.dir("generated/sources/sourceReplacement/java")
val processJavaSourceReplacement = tasks.register<Copy>("processJavaSourceReplacement") {
    from("src/main/java") {
        include("**/*.java")
        filter { line ->
            line.replace("@MOD_ID@", project.extra.get("mod_id") as String)
                .replace("@VERSION@", project.version.toString())
        }
    }
    into(sourceReplacementOutput)
}

sourceSets.named("main") {
    java.setSrcDirs(
        listOf(processJavaSourceReplacement.map { sourceReplacementOutput.get().asFile })
    )
}
