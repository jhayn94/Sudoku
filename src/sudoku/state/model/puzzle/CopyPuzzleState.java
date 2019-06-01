package sudoku.state.model.puzzle;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import sudoku.state.model.ApplicationModelState;

/**
 * This class updates the state of the application when the user copies the
 * puzzle to the clipboard
 */
public class CopyPuzzleState extends ApplicationModelState {

	private final boolean isGivensOnly;

	public CopyPuzzleState(final boolean isGivensOnly, final ApplicationModelState lastState) {
		super(lastState, false);
		this.isGivensOnly = isGivensOnly;
	}

	@Override
	public void onEnter() {
		final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		final String puzzleString = this.sudokuPuzzleValues.toString(this.isGivensOnly);
		final StringSelection textForClipboard = new StringSelection(puzzleString);
		clipboard.setContents(textForClipboard, null);
	}

}
