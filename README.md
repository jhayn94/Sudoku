
# Sudoku User Guide

## 0 Introduction: 
This application is a one-stop shop Sudoku application with a modern design. You can create puzzles with various configurations / difficulty levels, then solve them (manually, or with hints). In addition, most of the application is configurable to make it cater to your needs and preferences. 

This application is designed with diverse computer users in mind: nearly every feature or action should be possible with both mouse and keyboard, with just a few exceptions. So, whether you prefer to memorize keyboard shortcuts, point and click with the mouse, or anywhere in between, this app can support it!

The solving algorithm and hint generation components of the project are based on human-oriented solving tactics. So, you can get real, usable hints if you are stuck. In addition, the solver is fully configurable: you may define the order of solution techniques to better align with your preferred solving tactics, and avoid ones you dislike. (Or, you can disable them altogether and generate puzzles that don’t require a specific technique to solve.)

Overall, this application is pretty feature dense, and as such, this document is split up in to many sections and pages to try and cover all the different ways to do things. 

<TODO - enter table of contents>

## 1 Puzzle Management:
This chapter describes the various ways you can create and interact with puzzles.

### 1.1 Creating puzzles:
This application supports three main ways of creating puzzles. You may manually enter your own, use the puzzle generation mechanic, or paste a puzzle from the clipboard into the application.

#### 1.1.1 Entering a puzzle manually:
To manually input a puzzle, open the menu and select File | New Blank Puzzle (ALT+N). This will create a new puzzle with nothing in it. Now, you may enter your givens in the cells. Once done, select Edit | Set Givens (CTRL+P) to lock the givens as such. This is not strictly necessary, but it helps to prevent you from accidentally removing a given. In addition, given cells are a different font color, which is important when using some solving techniques. 

#### 1.1.2 Generating a puzzle:
To generate a puzzle, open the context menu and select New Random Puzzle (CTRL+N). This will create a new puzzle with the currently defined settings. See the sections Changing Settings | Puzzle Generation and Changing Settings | Solver for details on how to configure the puzzle generation. 

Puzzle generation is very open ended, and it is possible to create very complicated requirements for the puzzle generator. Many of these cases are identified and mentioned to the user, but all cases may not be discovered. Therefore, if it takes more than a handful of seconds to generate a puzzle, you should review your solver and puzzle settings for possible issues.

For pasting a puzzle into the application, see the section titled “Copying and Pasting Puzzles”.

### 1.2 Saving and Loading puzzles:
This application supports basic saving and loading of files. To save a file, select File | Save from the menu, then choose the file name and location. This is the same as with any other application.

Loading a puzzle is also similar to other programs. Select File | Open Puzzle... from the menu (CTRL+O). Then, you can pick the puzzle file to open. Note that at this time, this application only supports loading puzzles which were originally saved by this program.

### 1.3 Copying and Pasting Puzzles:
This application has support for both copying of and pasting into the sudoku grid. To copy, select either Edit | Copy All Cells (CTRL+C) or Edit | Copy Givens (CTRL+SHIFT+C) from the menu. The relevant puzzle string should now be in your clipboard.

To paste a puzzle into the application, simply have the puzzle in your clipboard, and select Edit | Paste (CTRL+V) from the menu. Note that only sudokus represented as 81 characters are supported (where an empty cell is a dot or a zero). For other inputs, nothing will happen.

### 1.4 Set Givens:
The Set Givens menu item (CTRL+P) is found in the Edit menu. Selecting it will lock all fixed cells as givens. This can be helpful because it prevents you from accidentally changing a given cell in a manually input puzzle.

### 1.5 Restart:
If you want to reset the puzzle to its initial given state, the Edit | Restart menu item will allow you to restart the puzzle. 


## 2 Hint system:
The hint system is designed for you to be able to request either a little or a lot of help. This is done by offering both vague and specific hints.

In general, the specific hint feature will show a through explanation of what elimination(s) can be made. Clicking the specific hint button will annotate the puzzle to give a visual explanation, as well as place a written one in the hint text area.

Similarly, the vague hint will display only the next step’s type (with no visual display on the puzzle).

The apply hint button progresses the sudoku puzzle to a state as if you had found the eliminations / placements described in the hint. In addition, the annotations and written explanations will be cleared.

The hide hint button also clears all annotations and written explanations, but does not apply the hint to the puzzle.

Note that the solver settings in Setting | Solver dictate the order in which steps are checked for possible hints to show the user. See the section Changing Settings | Solver for more details.


## 3 Puzzle Stats:
The puzzle stats view offers a general overview of the difficulty of the puzzle. It can also show the relative progress made, or tell if you if have made any errors.

The first row displays the difficulty of the puzzle. In order from easiest to hardest, this could be easy, medium, hard, very hard, or diabolical. The difficulty of a puzzle is determined by summing the rating of all steps in the solution path. Then, the Max Score for a difficulty usually determines a puzzle’s difficulty level. The main exception to this is that a puzzle’s difficulty cannot be lower than the hardest step in the solution path. Thus, if you have an easy puzzle with a Kraken Fish in the solution path, the puzzle’s difficulty is automatically Diabolical (or whichever difficulty you have set for this tactic’s difficulty). Finally, if a puzzle is solvable, it’s difficulty is displayed as “Invalid”.

It is important to emphasize that “the solution path” refers to the solution found by the solver in its current configuration. Obviously, there are many ways to reach the solution sometimes. So, a puzzle’s rating changes based on how you configure your solver. 

The second row displays a numeric score for the puzzle. This is a representation of the complexity of the puzzle and the techniques required to solve it. As stated above, a puzzle’s rating is just the rating for all steps on the solution path. As such, a puzzle’s rating is subjective based on your copy of the application because the rating of each step may be configured.

The third row shows the remaining rating for the puzzle. This could also be seen as a progress indicator of sorts, since this number decreases as you eliminate candidates or fill in cells. In addition, if you reach a state which can not be solved, this row will display “Invalid puzzle!” instead. This mainly occurs if you place the same digit in a house twice. In addition, if Auto Manage Candidates is enabled, this message also appears when you delete a candidate which belonged in the cell.

The score fields can behave strangely in some circumstances. These are not bugs, but a side effect of one of a few possible configurations. 

First, if Solve up to Technique is enabled, the base score of the puzzle will sometimes appear below the range set for the puzzle’s difficulty level. This is because the puzzle’s score is calculated from the puzzle state initially displayed to you (and not from the very beginning of the puzzle). Thus, the steps between the start of the puzzle and the initial state presented are not being considered.

Second, the remaining score behaves differently if Auto Manage Candidates is not checked. Normally, the puzzle solver uses the existing candidates displayed to calculate the remaining difficulty, because not every solution step places a cell. However, if candidates are not shown initially, the solver cannot tell if such eliminations have been made yet. Therefore, if Auto Manage Candidates is not checked, the remaining score can only update when a cell is placed or deleted. 

A number of configurations can be made which affect this view. See the settings sections for Difficulty and Show Puzzle Progress for more details.


## 4 Changing Settings:
In order to reach as wide of a user base as possible, much of the application can be configured to your liking. Each sub-section below has a corresponding menu item in the settings menu. Clicking “Save and Apply” in these dialogs will save the settings to your computer, apply them to the view (if necessary), and close the window. Since the settings are saved to your computer, these settings will persist if you close and re-open the application. Finally, the “Restore Defaults” button resets the settings widgets back to the defaults. However, these changes will not be saved unless you click “Save and Apply”.

### 4.1 Puzzle Generation Settings:
Puzzle generation settings are parameters that are basically required aspects or features of a new puzzle that would be generated in the future. This does not have any effect on already created puzzles.

#### 4.1.1 Difficulty:
The difficulty setting determines how hard newly generated puzzles will be. To change this, you may select the drop down and pick a new choice. In order of easiest to hardest, the options are easy, medium, hard, very hard, and diabolical.

#### 4.1.2 Require solution step:
With this setting you may require generated sudokus to contain a specific step somewhere in the solution. Note that whether a sudoku contains a step is determined by the current solver configuration. Since the solver never deviates from the defined order to check for techniques,  it is possible to not find the particular solution step if you use a harder tactic to pass over it. Therefore, it is suggested you either use the next setting Solve up to Technique, or configure the solver to match your solving strategy.

Be aware that certain configurations of this combined with the prior setting can produce impossible constraints on the puzzle generator. For instance, if you set the puzzle difficulty to easy, and then require puzzles to contain Uniqueness Test 1, the generator will not be able to satisfy these requirements. In this case, it will work indefinitely to try and satisfy them. It is suggested you review your configuration if it takes more than several seconds to create a puzzle.

#### 4.1.3 Solve up to Technique:
Solve up to Technique can only be enabled when a particular solution step is required. For instance, you can set the former setting to “Hidden Triple”. Then, if Solve up to Technique is enabled, the puzzle will be displayed in a state such that a hidden triple can be executed as the first step.

Note that this feature has some unexpected side effects on the difficulty rating of a puzzle. See the prior section titled “Puzzle Stats” for more details.

### 4.2 Difficulty Settings:
Difficulty settings allow you to change the definition of what it really means for a puzzle to be "medium" or "hard".

#### 4.2.1 Max rating:

The maximum rating for a difficulty can be modified in this view. This effectively changes how easy or hard a difficulty level is. There is no restriction that difficulty levels must remain in order. However, reordering difficulty levels is not recommended, as it is not tested and likely contains bugs. Lastly, note that diabolical’s max rating is effectively infinite, and it cannot be changed. 

### 4.3 Solver Settings:
In general, solver settings drive a majority of the application’s behavior. It affects puzzle generation, the hint system, and the puzzle stats.

#### 4.3.1 Disable a solving tactic:
Disabling a tactic will prevent the solver from giving you hints which use the tactic. In addition, that means that puzzles generated after disabling a technique can be solved without using said technique. However, this does not necessarily mean that the tactic could never be used, only that is it is not required to solve the puzzle. 

As stated before, this affects the difficulty and rating of a puzzle. If you prevent the solver from using easier tactics, it will be forced to use harder ones, which will make the rating for the same puzzle higher.

#### 4.3.2 Change the order of tactics:
To change the order in which the solver checks for tactics to apply, select that tactic in the list view on the left, and use the up and down arrows on the right side of the view.

#### 4.3.3 Change the difficulty level or rating:
Changing the difficulty level or rating of a solver tactic allows you to make the solver mirror your personal solving approach. For instance, if you struggle with a tactic, you can increase its difficulty or rating (or both). The opposite is true if you find a tactic easier than usual. 

In addition, changing the difficulty settings for a step may be necessary to satisfy puzzle generation requirements. This is because a puzzle’s difficulty level may not be lower than the hardest step in the solution path. For example, if you want an easy puzzle with an XY-Chain (which is very hard by default) you must change XY-Chain’s difficulty level to easy. 

In tandem with the previous paragraph, it may be necessary to lower the rating, too. A puzzle’s rating is the sum of all its steps. So, if a single step is rated 750 and an easy puzzle cannot be rated above 800, there is not much room to add more steps to the solution path.

### 4.4 Color Settings:
These settings are for changing the colors used for various annotations in the puzzle (such as filters).

#### 4.4.1 Filter color:
If the default color used to display filters is not suitable, you may change it to any other color. Note, however, that due to the dark blue and black text used in the grid, it is suggested that this color remain a light shade.


#### 4.4.2 Coloring colors:
There exist 5 pairs of colors which you can use to annotate the sudoku grid. By default, these are red, blue, purple, green, and orange. To change these colors, simply use the color picker for for the corresponding color you wish to change. By default, colors are paired such that each pair contains a dark and a light version of the color, which is most useful for coloring eliminations. While it is recommended to keep the color pairs as a light and a dark version, you can technically set any color for these settings.

#### 4.4.3 Hint colors:
In addition to the color pairs, there are also a number of colors used specifically to color candidates when annotating the screen with hints. There are 5 colors used for hints:

* Hint Color 1: the primary color used to show notable candidates used in a solution technique. For example, candidates part of hidden tuples or nodes in a single digit pattern (e.g. skyscraper). Also used for every other node in AICs.
* Hint Color 2: identifies notable or specific candidates that are particularily important for a hint. For example, fins of finned fish, or the non-endpoint nodes of a 2 string kite. Also used for every other node in AICs.
* Hint Color 3: Used very rarely. Identifies endo-fins of fish, and for one sector in Sue-de-Coq.
* Hint Color 4: Currently unused.
* Hint Color 5: Used only for cannibalism.
* Hint Delete Color: Used to indicate a candidate can be deleted based off of a hint.
* ALS Colors 1 - 4: Used to identify an ALS on the few occasions where they are used. Hint colors 2 and 3 are used to identify special candidates (i.e. the candidate designated XY or XZ in those ALS techniques).

### 4.5 Miscellaneous Settings:
These settings are for configurations that don't really belong to another specific category.

#### 4.5.1 Auto Manage Candidates:
By default, the application will fill all possible candidates when a puzzle is generated (through any of the 3 methods). In addition, as you set values in cells, candidates which are invalid will be removed. If you wish to perform this process on your own, unchecking this checkbox this will prevent the application from modifying candidates automatically. 

#### 4.5.2 Show Puzzle Progress:
By default, the application will maintain a remaining score for the puzzle. In addition, if you create a contradiction in the puzzle, such as by setting a value twice in a house, an “Invalid Puzzle!” warning will appear. If you find that this information gives away too much information, unchecking this checkbox will hide the information shown here.

#### 4.5.3 Use Digit Buttons for Mouse Actions:
By default, the application will not use the digit buttons in the lower left corner for candidate-based mouse actions. This is because it feels more intuitive to just click a candidate you want to modify. However, if you have difficulty precisely clicking these smaller view elements (or if you just prefer it this way), toggle this on to make the mouse actions ignore the candidate clicked, and use the active digit button instead.

_This only applies to the digit buttons in the lower left corner. The other digit buttons are for filters and are not affected by this setting. The same is true for the colored buttons; they are always used by the mouse._ 

## 5 Keyboard accessibility:
This application was designed to be a keyboard first program (i.e. no or minimal mouse use needed). As such, nearly every main feature has a designated keyboard control or shortcut (see hotkeys section). In addition, the main keyboard usage is described below.


### 5.1 The Basics: 
The arrow keys are used for navigating around the grid. An opaque cursor with a black outline shows the selected cell. To enter a digit into a cell, type that number. To clear a cell, type delete. Note, however, that if you have Automatically manage candidates checked in Miscellaneous settings, this will re-populate any candidates which are not explicitly disallowed. (So, any eliminations you made are overwritten.) To avoid this problem, it is recommended you utilize undo (CTRL+Z) and redo (CTRL+Y) instead. Lastly, to toggle a candidate for a cell, use CTRL+ that digit.

### 5.2 Coloring / Annotating:
Various features are available which apply colors to the sudoku grid. This is intended to assist you in identifying various patterns, chain, etc.

To color a cell, use the arrow keys to select it, and type any of A S D F or G. This will place a colored background on the cell. In addition, type SHIFT+ any of those 5 keys for the alternate version of that color. Typing any of these while another color is active will change the color. Typing the same key (or key combination) on a cell consecutively will return that cell to a non-colored background. Or, typing R will clear all cell colors (but not filters).

This application also supports coloring of candidates, provided that a cell is not fixed (or given), and that the given candidate is visible. The active candidate display near the bottom left of the screen indicates which candidate will be colored when using the keyboard. To change this by using the keyboard, use the = key to increment the value, - to decrement it, or ALT + a digit to just set it to that digit. _I'm not 100% please with how the mouse / keyboard controls are just kind of mixed in, but I couldn't come up with a better alternative. The main issue is that to color a candidate, you might need to press as many as 4 keys (CTRL + SHIFT + A + 1, for instance). So, I made the decision to have buttons instead._

To color a candidate, type any of CTRL+ A, S, D, F, or G to color the active candidate. Typing R will clear all candidate colors (but not filters).

_NOTE: The colored buttons on the lower left corner are not used by the keyboard. This is because the keyboard has lots more available buttons to press than just a few on the mouse._

### 5.3 Filtering:
This application also supports filtering / highlighting of cells that meet various criteria. This can assist you in finding more complicated patterns and eliminations. To highlight cells which could have a specific digit, press that F+digit key (F1 - F9). In addition, F10 highlights bivalue cells. Pressing the key again will un-highlight the cells. Lastly, you may cycle the active filter with the PERIOD (.) and COMMA (,) keys. PERIOD increases the filter by one (i.e. F1 -> F10); COMMA works in the downward direction.

### 5.4 Hints:
If you get stuck, F12 will display a specific hint in the hint text area. ALT+F12 will display only a partial hint: the technique name is shown instead of stating the specific eliminations made. Once a hint is shown, ENTER will apply the hint, and BACKSPACE will hide it. In addition, changing any cell in the puzzle will hide the hint.

### 5.5 Menu:
The menu is accessible using the keyboard. Use CTRL+M to open the keyboard. Then, the arrow keys allow you to navigate the menu, and enter will select the highlighted menu item. Note, however, that the settings menus will require use of a mouse.

Many other features are available with the keyboard, but their usage is more niche. See the list of hotkeys at the end of this readme for an exhaustive list of controls.


## 6 Mouse accessibility:
Although this application is designed domination for keyboard use, most features are available with the mouse. Some features are not supported by the mouse because they either result in too much clutter on the UI (and the UI has somewhat limited space left), or they proved to be clunky or complicated to use once implemented. Such exceptions will be noted in this section in bold text.

### 6.1 The Basics:
Select cells by clicking on them (note the mouse mode in the center left defaults to “Select cells”, this will be covered later). As before, an opaque cursor with a black outline shows the selected cell. Clicking a selected cell unselects it.

To set a number in a cell, *use the 1-9 keys*. To delete or clear a cell, *use the DELETE key*. Note, however, that if you have Automatically manage candidates checked in Miscellaneous settings, this will re-populate any candidates which are not explicitly disallowed. (So, any eliminations you made are overwritten.) Instead, it is recommended you use the undo and redo features. These can be accessed either from the Edit menu, or in the last row of the button grid in the left side of the view (they have the labels “<” and “>”).

### 6.2 Mouse Mode:
The behavior of the mouse is dictated by the mouse mode combo box in the left center of the screen. By default, “Select cells” is the active mouse mode . Other modes supported include “Toggle Candidates”, “Color Cells” and “Color Candidates”.
 
When “Color Cells” is the active mouse mode, clicking a cell will set the color of the cell to the color shown in the active color display, near the bottom left of the screen. Clicking the cell again will clear the color. To apply the alternate version of the active color, hold down SHIFT and click the cell.

A similar behavior is present for the “Color Candidates” mode, except that a candidate will be colored instead of the whole cell. The active candidate display shows the digit which will be colored. Note that the cell must not be fixed or given, and the candidate must be visible.

To change the active color used to color with the mouse, click the colored buttons in the control helper pane. To clear the colors, click the button labeled 'R'.

_NOTE: In the default configuration, the number buttons on the lower left corner are not used by the mouse. They are only used by the keyboard, since you would have to press too many keys at once to color candidates. See the setting Use _

### 6.3 Filtering:
This application also supports filtering / highlighting of cells that meet various criteria. This can assist you in finding more complicated patterns and eliminations. To highlight cells which could have a specific digit, click that digit’s button in the top left area of the application. In addition, the X|Y button highlights bivalue cells. Clicking a button again will un-highlight the cells.

### 6.4 Hints:
If you get stuck, the hint buttons in the top right of the screen can assist in finding a tricky step. Clicking the specific hint button will display a specific hint in the hint text area. The vague hint button will display only a partial hint: the technique name is shown instead of stating the specific eliminations made. 

To apply a hint after requesting it, click the apply hint button. In addition, the hide hint button will return the application to the pre-hint state without applying the hint. In addition, changing any cell in the puzzle will hide the hint.

### 6.5 Menu:
To access the menu, click the button in the top left, in the title bar.


## 7 List of hotkeys:

Key(s) | Action Invoked
-------|---------------
Arrow keys | Changes the selected cell.
1-9 | Sets that digit for the selected cell.
DELETE | Removes the fixed digit from the selected cell
CTRL+1-9 | Toggles that digit as a candidate for the selected cell.
A,S,D,F,G | Applies a color to the selected cell.
SHIFT+A,S,D,F,G | Applies an alternate color to the selected cell.
R | Reset cell and candidate colors.
CTRL+A,S,D,F,G | Applies a color to the active digit for the selected cell. Cell must be unfixed, not given, and the digit must be toggled on.
= | Increments the active digit.
\`- | Decrements the active digit.
ALT + 1-9 | Sets the digit as the active digit used for coloring candidates with the keyboard.
F1-F9 | Highlights cells that could have that digit (remove the 'F' to get the digit).
F10 | Highlights bivalue cells.
COMMA | If a filter / highlight is selected, cycle the active filter downward.
PERIOD | If a filter / highlight is selected, cycle the active filter upward.
F12 | Shows a specific hint.
ALT+F12 | Shows a vague hint.
ENTER | Applies the currently shown hint.
BACKSPACE | Hides the currently shown hint.
CTRL+M | Show the application menu.
CTRL+N | Creates a new random puzzle using the current settings.
ALT+N | Creates a new blank puzzle.
CTRL+O | Opens a puzzle.
ALT+F4 | Closes the application.
CTRL+Z | Undoes the last action.
CTRL+Y | Redoes the last undone action.
CTRL+C | Copies all fixed cells into clipboard.
CTRL+SHIFT+C | Copies given cells into clipboard.
CTRL+V | Pastes current clipboard as new puzzle.
CTRL+P | Sets all fixed cells as givens.
CTRL+U | Sets all given cells as not given (only fixed).
CTRL+H | Shows a brief about / help dialog.
CTRL+\` | Shows the hotkey dialog.
