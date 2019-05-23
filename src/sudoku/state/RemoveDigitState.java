package sudoku.state;

import org.apache.logging.log4j.util.Strings;

import javafx.scene.input.KeyCode;
import sudoku.view.puzzle.SudokuPuzzleCell;

/**
 * This class corresponds to a sudoku cell which is set by the user. Candidates
 * cannot be toggled, but the fixed digit may be changed or deleted.
 */
public class RemoveDigitState extends ApplicationModelState {

	public RemoveDigitState(final KeyCode keyCode, final ApplicationModelState lastState) {
		super(lastState);
		this.lastKeyCode = keyCode;
	}

	@Override
	public void onEnter() {
		final SudokuPuzzleCell selectedCell = this.getSelectedCell();
		selectedCell.setCandidatesVisible(true);
		selectedCell.setFixedDigit(Strings.EMPTY);
		this.updateCssClass(UNFIXED_CELL_CSS_CLASS);
	}

}
