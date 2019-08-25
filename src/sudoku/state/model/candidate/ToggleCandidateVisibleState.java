package sudoku.state.model.candidate;

import org.apache.logging.log4j.util.Strings;

import javafx.scene.input.KeyCode;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.puzzle.SudokuPuzzleCell;

/**
 * This class updates the state of the application to reply to toggle a
 * candidate's visibility in the active cell.
 */
public class ToggleCandidateVisibleState extends ApplicationModelState {

	private static final String NUMPAD_REPLACE_TEXT = "Numpad ";

	public ToggleCandidateVisibleState(final KeyCode keyCode, final ApplicationModelState lastState) {
		super(lastState, true);
		this.lastKeyCode = keyCode;
	}

	@Override
	public void onEnter() {
		final int pressedDigit = Integer.parseInt(this.lastKeyCode.getName().replace(NUMPAD_REPLACE_TEXT, Strings.EMPTY));
		final SudokuPuzzleCell selectedCell = this.getSelectedCell();
		this.toggleCandidateActiveForCell(pressedDigit, selectedCell);
		this.updateRemainingScoreForPuzzle();
	}

}
