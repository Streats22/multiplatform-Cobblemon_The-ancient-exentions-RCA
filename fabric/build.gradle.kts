plugins {
    id("com.gradleup.shadow")
    id("dev.architectury.loom")
    id("architectury-plugin")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    enableTransitiveAccessWideners.set(true)
    silentMojangMappingsLicense()

    mixin {
        defaultRefmapName.set("mixins.${project.name}.refmap.json")
    }
}

val shadowCommon: Configuration by configurations.creating

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")

    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
    modImplementation(fabricApi.module("fabric-command-api-v2", property("fabric_api_version").toString()))
    modImplementation(fabricApi.module("fabric-screen-handler-api-v1", property("fabric_api_version").toString()))
    modImplementation(fabricApi.module("fabric-networking-api-v1", property("fabric_api_version").toString()))
    modImplementation(fabricApi.module("fabric-object-builder-api-v1", property("fabric_api_version").toString()))
    modImplementation(fabricApi.module("fabric-item-group-api-v1", property("fabric_api_version").toString()))
    modImplementation(fabricApi.module("fabric-rendering-v1", property("fabric_api_version").toString()))

    val jeiCommonApi =
        "mezz.jei:jei-${property("minecraft_version")}-common-api:${property("jei_version")}"
    val jeiFabricApi =
        "mezz.jei:jei-${property("minecraft_version")}-fabric-api:${property("jei_version")}"
    fun optionalDev(dep: String) {
        modCompileOnly(dep)
        compileOnly(dep)
        modLocalRuntime(dep)
    }
    optionalDev(jeiCommonApi)
    optionalDev(jeiFabricApi)
    modRuntimeOnly("mezz.jei:jei-${property("minecraft_version")}-fabric:${property("jei_version")}")

    //needed for cobblemon
    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin")}")
    modImplementation("com.cobblemon:fabric:${property("cobblemon_version")}") { isTransitive = false }

    implementation(project(":common", configuration = "namedElements"))
    "developmentFabric"(project(":common", configuration = "namedElements"))
    shadowCommon(project(":common", configuration = "transformProductionFabric"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:${property("junit_version")}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${property("junit_version")}")
}

tasks {
    test {
        useJUnitPlatform()
    }

    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand(project.properties)
        }

    }

    jar {
        archiveBaseName.set("${rootProject.property("archives_base_name")}-${project.name}")
        archiveClassifier.set("dev-slim")
    }

    shadowJar {
        archiveClassifier.set("dev-shadow")
        archiveBaseName.set("${rootProject.property("archives_base_name")}-${project.name}")
        configurations = listOf(shadowCommon)
    }

    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        archiveBaseName.set("${rootProject.property("archives_base_name")}-${project.name}")
        archiveVersion.set("${rootProject.version}")
    }
}
