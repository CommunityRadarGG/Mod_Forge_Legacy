import dev.architectury.pack200.java.Pack200Adapter

val versionText: String by extra
val modIdText: String by extra

dependencies {
    include(project(":common"))
    implementation(project(":common"))

    minecraft(rootProject.libs.minecraft1122)
    mappings(rootProject.libs.mcpMappings1122)
    forge(rootProject.libs.forge1122)
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
        inputs.property("mc_version", rootProject.libs.versions.minecraft1122.get())
        inputs.property("mod_id", modIdText)

        filesMatching("mcmod.info") {
            expand(
                mapOf(
                    "version" to versionText,
                    "mc_version" to rootProject.libs.versions.minecraft1122.get(),
                    "mod_id" to modIdText
                )
            )
        }
    }
}
