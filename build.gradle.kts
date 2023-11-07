import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.withType

plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

repositories { 
    // Use Maven Central for resolving dependencies.
    gradlePluginPortal()
    mavenCentral()
    
    //Jitpack.io
    maven { url = uri("https://jitpack.io") }
    
    flatDir {
		dirs("libs")
	}
    
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

	implementation(
		fileTree("libs/") {
			include("*.jar")
		}
	)
}

application {
	mainClass = "io.azraein.eden.Main"
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    val uberJar by creating(ShadowJar::class) {
        archiveClassifier.set("release")
        archiveVersion.set("0.0.1")

        from(sourceSets.main.get().output)

		from(project.configurations.compileClasspath)

		mergeServiceFiles()

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
