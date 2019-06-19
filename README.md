# Sudoku README
This application is a one-stop shop Sudoku application with a modern design. You can create puzzles with various configurations / difficulty levels, then solve them (manually, or with hints). In addition, most of the application is configurable to make it cater to your needs and preferences. 

This application is designed with diverse computer users in mind: nearly every feature or action should be possible with both mouse and keyboard, with just a few exceptions. So, whether you prefer to memorize keyboard shortcuts, point and click with the mouse, or anywhere in between, this app can support it!

The solving algorithm and hint generation components of the project are based on human-oriented solving tactics. So, you can get real, usable hints if you are stuck. In addition, the solver is fully configurable: you may define the order of solution techniques to better align with your preferred solving tactics, and avoid ones you dislike. (Or, you can disable them altogether and generate puzzles that don’t require a specific technique to solve.)

Overall, this application is pretty feature dense, and as such, I suggest you view the UserGuide.md document, which is split up in to many sections to try and cover all the different ways to do things. 

### Licensing + Usage:

Short version:  You are free to distribute, use, or add onto this work. Should you add onto it, I would love to receive feedback + suggestions for integrating it into this repository. However, in all circumstances, I insist to be credited for components of the project which I implemented (i.e. the version in this repository). Furthermore, in no case shall another person use this work to produce a profit, whether through direct utilization of this software or an extension or derivation of it.

Long version: See end of README.md for a formal copyright notice.

### Overview:
As the description suggests, this is a standalone Java application for playing, generating and analyzing Sudoku puzzles. 

See sampleScreenshot.png for a quick visual of the application.
  
### Application setup for end users:
  1) In ..\Sudoku\, find and download Sudoku-X.X.X.zip from this respository, where the X's are the latest version number available.
  2) Extract the .zip contents to the desired location on your computer. There should be a .jar file, and two additional folders (data and resources). _You will need to grant the necessary permissions to this application or your user account, depending on your operating system._
  3) In ..\Sudoku-X.X.X\data\binaries\, find Quicksand-Bold.otf and run it. This will download the application's font to your computer. This step is technically optional, but the style for the application is aligned with this font's size, so other fonts might not work 100% correctly. Do the same thing for Quicksand-Regular.otf.
  4) Execute Sudoku-X.X.X.jar. This should launch the base application. _Again, you might need to modify permissions of your user profile to allow execution of this program. Also, I suggest you not run this from your browser's download bar / window, since it seems to be way slower. Instead, you should open your download destination folder and try running from there._
  5) Within the application open the context menu -> Help for more detailed usage instructions.
  
### Application setup for developers:
  1) Clone this repository. _NOTE: I used Eclipse for development, so guidelines will be defined for this IDE. However, you could use any other Java IDE as desired._
  2) In ..\Sudoku\data\binaries\, find Quicksand-Bold.otf and run it. This will download the application's font to your computer. This step is technically optional, but the style for the application is aligned with this font's size, so other fonts might not work 100% correctly. Do the same thing for Quicksand-Regular.otf.
  3) In Eclipse, open Project -> Properties -> Java Build Path -> Libraries. Click 'Add External JARs'. Navigate to ..\Sudoku\data\binaries\. Then add all 5 .jars to your source path.
  4) Click "Apply" or "Apply and Close", depending on your Eclipse version.
  5) After a clean, you should be able to run the base class "Sudoku.java" from within Eclipse.
  
  
### Copyright Notice:
  
Copyright © jhayn94. All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions, a link to the original repository and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions, a link to the original repository and the following disclaimer in the (uncompressed) documentation and/or other materials provided with the distribution.
Neither the name of the copyrighter nor their work may be used to endorse, promote, or profit from products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS “AS IS” AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  
  
  
