# Eden Bug List
[STATUS] - CLASS - LINE - SEVERITY - DATE FOUND - BUG 

Status Types:

	* WILD = Don't currently know what causes it.
	* WOP = Working on patching
	* FIX = Fixed

------------------------------------------
[FIX] - GRADLE? - UberJar Task? - SEVERE - 11/4/2023 - 10:47PM - The Ikonli Library seems to break once the project is built using the assemble task. The UberJar Task depends on the assemble task and for the most part produces a working jar. With the unfortunate caveat that the IKonli FontIcons are completely gone and the console produces a leauge of errors. 

The fix was to incorpate ShadowJar and uses its mergeServiceFiles() method to correctly compile the project.

<details>
	<summary>
	
		```kotlin
		
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
		
		```
	
	</summary>
</details>

[WIP] - EdenMessageBox - 171 - Medium - 11/10/2023 - 2:27AM - When clicking through the Users List, the program adds EdenUsers to an observable list, that ultimately adds EdenConvoBubbles. There can be 4 convoBubbles at a time and when a 5th is added, the program removes the first bubble, then adds the convo to the list. We get a ConcurrentModificationException when more than 4 bubbles are added. Fixing this bug is realtively easy, just fix all the ConcurrentModificationExceptions.





