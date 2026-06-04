plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
}

architectury {
    common("neoforge", "fabric")
}

loom {
    silentMojangMappingsLicense()
    splitEnvironmentSourceSets()

    mods {
        register("ancient_extensions") {
            sourceSet(sourceSets.getByName("main"))
            sourceSet(sourceSets.getByName("client"))
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("com.cobblemon:mod:${property("cobblemon_version")}") { isTransitive = false }

    testImplementation("org.junit.jupiter:junit-jupiter-api:${property("junit_version")}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${property("junit_version")}")
}

tasks.test {
    useJUnitPlatform()
}

/** Eclipse/VS Code expect these output dirs; Kotlin client is often empty and client resources may be absent. */
tasks.register("ensureIdeClientOutputs") {
    group = "ide"
    dependsOn("compileClientJava")
    doLast {
        layout.buildDirectory.dir("classes/kotlin/client").get().asFile.mkdirs()
        layout.buildDirectory.dir("resources/client").get().asFile.mkdirs()
    }
}

tasks.named("eclipse") {
    dependsOn("ensureIdeClientOutputs")
}
