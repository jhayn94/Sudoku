# TESV_Alchemy_Recipe_Browser
A standalone JavaFX application for examining alchemy recipes in TESV: Skyrim

### Licensing + Usage:

Short version:  You are free to distribute, use, or add onto this work. Should you add onto it, I would love to receive feedback + suggestions for integrating it into this repository. However, in all circumstances, I insist to be credited for components of the project which I implemented (i.e. the version in this repository). Furthermore, in no case shall another person use this work to produce a profit, whether through direct utilization of this software or an extension or derivation of it.

Long version: See end of README.md for a formal copyright notice.

### Overview:
As the description suggests, this is a standalone Java application for examining alchemy recipes in Skyrim. The general idea of the application is as follows:
  1) Define your character's stats with the 'Character' tab (currently vanilla Skyrim and Ordinator are supported).
  2) Select 1 - 5 effects which must be on the potion, and search.
  3) After returning results, you may define additional criteria, such as requiring all ingredients to be from a certain extension (DLC) of the game, or requiring all ingredients to be harvestable.
  
See ..\ARB_Data\sampleScreenshot.png for a quick visual of the application. Lastly, a more detailed overview of the application is available in the context menu -> Help.
  
### Application setup for end users:
  1) In ..\TESV_Alchemy_Recipe_Browser\ARB_Data\binaries\, find and download TESV_Alchemy_Recipe_Browser-X.X.jar, where the X's are the latest version number available. Also download FuturaCondensedBQ-Medium.otf from this directory.
  2) Save to desired location on your computer. when run, the application (the .jar file) will create a folder called ARB_Data in the same folder. This folder is used to store logs, as well as read and write to a save file. _You will need to grant the necessary permissions to this application or your user account, depending on your operating system._
  3) Find FuturaCondensedBQ-Medium.otf and run it. This will download the application's font to your computer. This step is technically optional, but the style for the application is aligned with this font's size, so other fonts might not work 100% correctly.
  4) Execute TESV_Alchemy_Recipe_Browser.jar. This should launch the base application. _Again, you might need to modify permissions of your user profile to allow execution of this program._
  5) Within the application open the context menu -> Help for more detailed usage instructions.
  
### Application setup for developers:
  1) Clone this repository. _NOTE: I used Eclipse for development, so guidelines will be defined for this IDE. However, you could use any other Java IDE as desired._
  2)  In ..\TESV_Alchemy_Recipe_Browser\ARB_Data\binaries\, find FuturaCondensedBQ-Medium.otf and run it. This will download the application's font to your computer. This step is technically optional, but the style for the application is aligned with this font's size, so other fonts might not work 100% correctly.
  3) In Eclipse, open Project -> Properties -> Java Build Path -> Libraries. Click 'Add External JARs'. Navigate to ..\TESV_Alchemy_Recipe_Browser\ARB_Data\binaries\. Then add jfxrt.jar and all 3 .jars that start with log4j.
  4) Click "Apply" or "Apply and Close", depending on your Eclipse version.
  5) After a clean, you should be able to run the base class "ARB.java" from within Eclipse.
  
  
### Copyright Notice:
  
Copyright © jhayn94. All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions, a link to the original repository and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions, a link to the original repository and the following disclaimer in the (uncompressed) documentation and/or other materials provided with the distribution.
Neither the name of the copyrighter nor their work may be used to endorse, promote, or profit from products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS “AS IS” AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  
  
  
