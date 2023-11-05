plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

javafx {
	
	version = "21.0.1"
	modules = listOf("javafx.controls", "javafx.media")
	
}

dependencies {
	
	// Font Icons - Ikonli
	implementation("org.kordamp.ikonli:ikonli-javafx:12.3.1")
	implementation("org.kordamp.ikonli:ikonli-material2-pack:12.3.1")
	
	// TinyLog2 
	implementation("org.tinylog:tinylog-api:2.6.2")
	implementation("org.tinylog:tinylog-impl:2.6.2")
	
	// ControlFX
	implementation("org.controlsfx:controlsfx:11.1.2")
	
	// DesktopPaneFX
	implementation("org.kordamp.desktoppanefx:desktoppanefx-core:0.15.0")
	
	
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    val uberJar by creating(Jar::class) {
        archiveClassifier.set("uber")

        from(sourceSets.main.get().output)

        dependsOn(configurations.runtimeClasspath)

        from({
            configurations.runtimeClasspath.get()
                    .filter { it.name.endsWith(".jar") }
                    .map { zipTree(it) }
        })
        
        duplicatesStrategy = DuplicatesStrategy.INCLUDE // Set the strategy to include duplicates
        
       	manifest {
			   attributes["Manifest-Version"] = "1.0"
			   attributes["Main-Class"] = "io.azraein.eden.Main"
		   }
        
    }

    assemble {
        dependsOn(uberJar)
    }
}
