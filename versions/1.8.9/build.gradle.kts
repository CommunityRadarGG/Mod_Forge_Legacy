import dev.architectury.pack200.java.Pack200Adapter

val versionText: String by extra
val modIdText: String by extra

dependencies {
    implementation(project(":common"))

    minecraft(rootProject.libs.minecraft189)
    mappings(rootProject.libs.mcpMappings189)
    forge(rootProject.libs.forge189)
}

loom {
    forge {
        pack200Provider.set(Pack200Adapter())
    }
}

tasks {
    withType<ProcessResources> {
        // https://github.com/gradle/gradle/issues/861
        inputs.property("version", versionText)
        inputs.property("mc_version", libs.versions.minecraft189.get())
        inputs.property("mod_id", modIdText)

        filesMatching("mcmod.info") {
            expand(
                mapOf(
                    "version" to versionText,
                    "mc_version" to libs.versions.minecraft189.get(),
                    "mod_id" to modIdText
                )
            )
        }
    }
}
