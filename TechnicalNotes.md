
# Technical Documentation / Notes:

This document serves to track known bugs and possible future features.

## Known Bugs:

### JavaFX Bugs:

These bugs are contained within the JavaFX code, so I do not have a planned resolution for them.
* Windows key + left and right arrow shortcuts don’t work (see: https://bugs.openjdk.java.net/browse/JDK-8215545)
* Combo box dropdown disappears when pressing space (see: https://bugs.openjdk.java.net/browse/JDK-8087549)
* Combo box is reset on typing the first character when using a FilteringList (see: https://bugs.openjdk.java.net/browse/JDK-8145517)
	* Originally, some combo boxes used this implementation. However, it was removed because the bug was very irritating.
	
### Project Bugs:

These bugs are contained within my project code. I will note with each one the resolution plan, if any.

* Windows key + up and down arrow shortcuts do not properly change the visual state of the application.
	* I think I'm going to leave this as is, unless it is shown to be problematic for others. I don't think this has any significant impact on the application's usability.
* Next bug goes here.


## Planned / Proposed Features:

This is a list of various possible or suggested features I have received (or I myself have considered).

* Ability to draw arrows / links on the screen. This would require a new mouse mode.
* Ability to drag and drop solver settings to reorder them.