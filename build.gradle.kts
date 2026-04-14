plugins {
    id("net.weavemc.gradle") version "1.1.0"
}

group = "dev.alxx"
version = "0.3.0"

base {
    archivesName.set("trenbolone-bridgonate")
}

weave {
    configure {
        name = "Trenbolone Bridgonate"
        modId = "trenbolonebridgonate"
        entryPoints = listOf("dev.alxx.keepsprint.KeepSprintMod")
        mixinConfigs = listOf("keepsprint.mixins.json")
        mcpMappings()
    }
    version("1.8.9")
}

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven/")
    // Check available packages at https://gitlab.com/weave-mc/weave/-/packages/
    maven("https://gitlab.com/api/v4/projects/80566527/packages/maven")
}

dependencies {
    implementation("net.weavemc:loader:1.1.0")
    implementation("net.weavemc:internals:1.1.0")
    implementation("net.weavemc.api:api:1.1.0")
    implementation("net.weavemc.api:api-v1_8:1.1.0")

    compileOnly("org.spongepowered:mixin:0.8.5")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}

java {
    withSourcesJar()

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks.test {
    useJUnitPlatform()
}
