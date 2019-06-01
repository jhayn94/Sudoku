package sudoku.view.util;

/**
 * This class contains constants that are shown as tool tips in the application.
 */
public class TooltipConstants {

	public static final String UNDO = "Undoes the most recently completed action.";

	public static final String REDO = "Redoes the most recently undone action.";

	public static final String MOUSE_MODE = "Determines what the mouse does when it clicks in the Sudoku.";

	public static final String ACTIVE_CANDIDATE = "Determines which candidate is used for various actions where it is otherwise not explicit.";

	public static final String ACTIVE_COLOR = "Determines which color is used for various actions where it is otherwise not explicit.";

	public static final String DIFFICULTY_DISPLAY = "The relative difficulty of the current puzzle.";

	public static final String RATING = "A numeric difficulty rating for the current puzzle.";

	public static final String REMAINING_RATING = "The difficulty of the remaining solution steps.";

	public static final String VAGUE_HINT = "Display a possible next step, with minimal detail.";

	public static final String SPECIFIC_HINT = "Display a possible next step, with a full written and visual explanation.";

	public static final String APPLY_HINT = "Apply the current hint to the puzzle.";

	public static final String APPLY_FILTER_PREFIX = "Highlight all cells that could have a ";

	public static final String APPLY_FILTER_SUFFIX = ".";

	public static final String APPLY_BIVALUE_FILTER = "Highlight all bi-value cells.";

	public static final String HIDE_HINT = "Hide the hint display and annotations.";

	public static final String AUTO_MANAGE_CANDIDATES = "Check this to have the application automatically add and remove candidates when you change the value of a cell.";

	public static final String SHOW_PUZZLE_PROGRESS = "Check this to receive progress updates each time you make a move.";

	public static final String MUST_CONTAIN = "If non-empty, generated puzzles must have this technique.";

	public static final String SOLVE_UP_TO = "Check this to solve the puzzle so the above step is a next possible step.";

	public static final String MAX_DIFFICULTY_SCORE_PREFIX = "The highest number a puzzle of '";

	public static final String MAX_DIFFICULTY_SCORE_SUFFIX = "' difficulty is allowed to have..";

	private TooltipConstants() {
		// Private constructor to prevent instantiation.
	}
}
