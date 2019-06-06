package sudoku.view.util;

/**
 * This class contains constants that are shown as labels or titles in the
 * application.
 */
public class LabelConstants {

	public static final String APPLICATION_TITLE = "Sudoku";

	public static final String FILE = "File";

	public static final String NEW_PUZZLE = "New Puzzle";

	public static final String NEW_PUZZLE_WITH_DIALOG = "New Puzzle...";

	public static final String NEW_BLANK_PUZZLE = "New Blank Puzzle";

	public static final String OPEN = "Open Puzzle...";

	public static final String SAVE = "Save";

	public static final String SAVE_AS = "Save As...";

	public static final String CLOSE = "Close";

	public static final String EDIT = "Edit";

	public static final String RESTART = "Restart";

	public static final String UNDO_LONG = "Undo";

	public static final String REDO_LONG = "Redo";

	public static final String SETTINGS = "Settings";

	public static final String PUZZLE_GENERATION = "Puzzle Generation";

	public static final String DIFFICULTY = "Difficulty";

	public static final String SOLVER = "Solver";

	public static final String COLORS = "Colors";

	public static final String MISCELLANEOUS = "Miscellaneous";

	public static final String HELP = "Help";

	public static final String BIVALUE_CELL = "X|Y";

	public static final String UNDO = "<";

	public static final String REDO = ">";

	public static final String MOUSE_MODE = "Mouse:";

	public static final String SELECT_CELLS = "Select cells";

	public static final String TOGGLE_CANDIDATES = "Toggle candidates";

	public static final String COLOR_CELLS = "Color cells";

	public static final String COLOR_CANDIDATES = "Color candidates";

	public static final String VAGUE_HINT = "Vague";

	public static final String SPECIFIC_HINT = "Specific";

	public static final String APPLY_HINT = "Apply";

	public static final String HIDE_HINT = "Hide";

	public static final String HINT = "Hints:";

	public static final String OK = "OK";

	public static final String OPEN_FILE = "Open Puzzle File";

	public static final String SAVE_FILE = "Save Puzzle File";

	public static final String NO_MOVES = "No possible moves found! Please double check the currently"
			+ " set cells for contradictions. Otherwise, there might not be enough givens to solve the puzzle.";

	public static final String VAGUE_HINT_PREFIX = "Possible solution step: ";

	public static final String SET_GIVENS = "Set Givens";

	public static final String MISCELLANEOUS_SETTINGS = "Miscellaneous Settings";

	public static final String COLOR_SETTINGS = "Color Settings";

	public static final String PUZZLE_GENERATION_SETTINGS = "Puzzle Generation Settings";

	public static final String SOLVER_SETTINGS = "Solver Settings";

	public static final String DIFFICULTY_SETTINGS = "Difficulty Settings";

	public static final String SAVE_AND_APPLY = "Save and Apply";

	public static final String AUTO_MANAGE_CANDIDATES = "Automatically Manage Candidates";

	public static final String FILTERED_CELL_COLOR = "Filter: ";

	public static final String COLOR_PAIR = "Color Pair ";

	public static final String RESTORE_DEFAULTS = "Restore Defaults";

	public static final String EASY = "Easy";

	public static final String MEDIUM = "Medium";

	public static final String HARD = "Hard";

	public static final String VERY_HARD = "Very Hard";

	public static final String DIABOLICAL = "Diabolical";

	public static final String MUST_CONTAIN = "Must Contain:";

	public static final String SOLVE_UP_TO = "Solve up to Technique";

	public static final String RATING = "Rating:";

	public static final String REMAINING_RATING = "Remaining:";

	public static final String SHOW_PUZZLE_PROGRESS = "Show Puzzle Progress";

	public static final String COPY_GIVENS = "Copy Givens";

	public static final String COPY_CELLS = "Copy All Cells";

	public static final String PASTE = "Paste";

	public static final String ENABLED = "Enabled:";

	public static final String INVALID_PUZZLE = "Invalid Puzzle!";

	public static final String INVALID = "Invalid";

	public static final String CANCEL = "Cancel";

	public static final String INVALID_SETTINGS = "Invalid Settings";

	public static final String STEP_HARDER_THAN_PUZZLE_DIFFICULTY = "The required step's difficulty may not exceed "
			+ "the selected puzzle difficulty. Please either select a higher difficulty, or decrease the "
			+ "difficulty level of the step.";

	public static final String STEP_INACTIVE = "The required step may not be disabled. "
			+ "Please enable the step, or choose a different one.";

	public static final String OVER_SCORE_LIMIT = "The base score of the required step is higher than the maximum "
			+ "score for the current difficulty. Please adjust the score of the step or difficulty level.";

	public static final String GENERATING_PUZZLE_TITLE = "Generating Puzzle";

	public static final String GENERATING_PUZZLE_MESSAGE = "Generating a new puzzle. This may take a few seconds. "
			+ "If generation takes more than 10 - 15 seconds, your puzzle requirements might be too strict.";

	public static final String RETRY_GENERATION = "Puzzle generation resulted in an error. Please retry generation. "
			+ "If the problem persists, check the log file.";

	public static final String ABOUT = "About";

	public static final String ABOUT_CONTENT = "This application is a one-stop shop Sudoku application with a modern "
			+ "design. You can create puzzles with various configurations / difficulty levels, then solve them (manually, or "
			+ "with hints). In addition, most of the application is configurable to make it cater to your needs and preferences."
			+ "\r\n\r\nThis application is designed with diverse computer users in mind: nearly every feature or action "
			+ "should be possible with both mouse and keyboard, with just a few exceptions. So, whether you prefer to "
			+ "memorize keyboard shortcuts, point and click with the mouse, or anywhere in between, this app can support it!"
			+ "\r\n\r\nThe solving algorithm and hint generation components of the project are based on human-oriented "
			+ "solving tactics. So, you can get real, usable hints if you are stuck. In addition, the solver is fully "
			+ "configurable: you may define the order of solution techniques to better align with your preferred solving "
			+ "tactics, and avoid ones you dislike. (Or, you can disable them altogether and generate puzzles that don’t "
			+ "require a specific technique to solve.)\r\n\r\nOverall, this application is pretty feature dense, and as such, "
			+ "it is suggested that you view the README in the GitHub project for the full user guide:\r\n\r\n"
			+ "https://github.com/jhayn94/Sudoku/blob/master/README.md";

	public static final String HOTKEYS = "Hotkeys";

	public static final String UNLOCK_GIVENS = "Unlock All Givens";

	public static final String HINT_COLOR = "Hint Color ";

	public static final String ALS_COLOR = "ALS Color ";

	public static final String HINT_DELETE_COLOR = "Delete Color:";

	public static final String PUZZLE_SOLVED = "Puzzle is already solved!";

	// For the reset button.
	public static final String R = "R";

	private LabelConstants() {
		// Private constructor to prevent instantiation.
	}
}
