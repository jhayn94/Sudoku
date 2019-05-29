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

	public static final String AUTO_MANAGE_CANDIDATES = "Check this to have the application automatically add and remove candidates when you change the value of a cell.";

	private TooltipConstants() {
		// Private constructor to prevent instantiation.
	}
}
