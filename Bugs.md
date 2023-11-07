# Eden Bug List
[STATUS] - CLASS - LINE - SEVERITY - DATE FOUND - BUG 

Status Types:

	* WILD = Don't currently know what causes it.
	* WOP = Working on patching
	* FIX = Fixed

------------------------------------------
[FIX] - GRADLE? - UberJar Task? - SEVERE - 11/4/2023 - 10:47PM - The Ikonli Library seems to break once the project is built using the assemble task. The UberJar Task depends on the assemble task and for the most part produces a working jar. With the unfortunate caveat that the IKonli FontIcons are completely gone and the console produces a leauge of errors. 

The fix was to incorpate ShadowJar and uses its mergeServiceFiles() method to correctly compile the project. 

