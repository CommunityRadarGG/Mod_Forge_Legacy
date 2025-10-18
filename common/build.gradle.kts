import org.gradle.kotlin.dsl.libs

base {
    archivesName.set("common")
}

@Suppress("VulnerableLibrariesLocal") // some libs need to stay at the game versions
dependencies {
    compileOnly(rootProject.libs.jetbrainsJavaAnnotations)
    compileOnly(rootProject.libs.gson)
    compileOnly(rootProject.libs.log4j)
}
