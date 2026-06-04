import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java")
    id("java-library")
    kotlin("jvm") version("2.2.20")
    id("com.gradleup.shadow") version "9.3.1" apply false
    id("dev.architectury.loom") version("1.11-SNAPSHOT") apply false
    id("architectury-plugin") version("3.4-SNAPSHOT") apply false
}

val sharedAssetRoots = listOf(
    "assets/ancient_extensions",
    "data/ancient_extensions",
    "data/cobblemon",
)

tasks.register("verifySharedAssets") {
    group = "verification"
    description = "Fail if Fabric or NeoForge define duplicate assets/data (must live in :common only)."
    doLast {
        fun hasRealFiles(dir: java.io.File): Boolean =
            dir.exists() && dir.walkTopDown().any { it.isFile && it.name != ".DS_Store" }

        val forbidden = listOf("fabric", "neoforge").flatMap { loader ->
            sharedAssetRoots.map { root ->
                file("$loader/src/main/resources/$root")
            }
        }.filter { hasRealFiles(it) }
        if (forbidden.isNotEmpty()) {
            error(
                "Loader-specific resource folders are not allowed. Move these into common/src/main/resources:\n" +
                        forbidden.joinToString("\n") { it.absolutePath }
            )
        }
    }
}

tasks.named("check") {
    dependsOn(tasks.named("verifySharedAssets"))
}

subprojects {
    tasks.matching { it.name == "build" }.configureEach {
        dependsOn(rootProject.tasks.named("verifySharedAssets"))
    }
}

// Shared IDE classpath wiring + setupIde task for every Loom subproject (:common, :fabric, :neoforge).
subprojects {
    plugins.withId("dev.architectury.loom") {
        afterEvaluate {
            tasks.register("setupIde") {
                group = "ide"
                description = "Generate Eclipse/VS Code metadata for this module."
                val extra = if (project.name == "common") {
                    listOf("ensureIdeClientOutputs")
                } else {
                    emptyList()
                }
                dependsOn(listOf("eclipse", "genEclipseRuns", "vscode") + extra)
            }

            fun Configuration.extendsOptionalCompileConfigs() {
                configurations.findByName("modCompileOnly")?.let { extendsFrom(it) }
                configurations.findByName("compileOnly")?.let { extendsFrom(it) }
                configurations.findByName("modLocalRuntime")?.let { extendsFrom(it) }
            }

            configurations.findByName("compileClasspath")?.extendsOptionalCompileConfigs()
            configurations.findByName("clientCompileClasspath")?.extendsOptionalCompileConfigs()
        }
    }
}

tasks.register("setupIde") {
    group = "ide"
    description = "Refresh IDE metadata for :common, :fabric, and :neoforge."
    dependsOn(
        ":common:setupIde",
        ":fabric:setupIde",
        ":neoforge:setupIde",
    )
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    version = project.properties["mod_version"]!!
    group = project.properties["maven_group"]!!

    repositories {
        mavenCentral()
        maven("https://artefacts.cobblemon.com/releases/")
        maven("https://maven.blamejared.com/")
        maven("https://api.modrinth.com/maven")
    }

    tasks {
        test {
            useJUnitPlatform()
        }

        java {
            withSourcesJar()
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }

        compileJava {
            options.release = 21
        }

        compileKotlin {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_21)
            }
        }
    }
}

