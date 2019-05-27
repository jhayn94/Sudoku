package sudoku.state.model.cell;

import org.apache.logging.log4j.util.Strings;

import javafx.scene.input.KeyCode;
import sudoku.state.ApplicationModelState;
import sudoku.view.puzzle.SudokuPuzzleCell;

/**
 * This class updates the state of the application when the user removes a set
 * digit from the cell. This will not work if the cell was given.
 */
public class RemoveDigitState extends ApplicationModelState {

	public RemoveDigitState(final KeyCode keyCode, final ApplicationModelState lastState) {
		super(lastState, true);
		this.lastKeyCode = keyCode;
	}

	@Override
	public void onEnter() {
		final SudokuPuzzleCell selectedCell = this.getSelectedCell();
		if (!selectedCell.isCellGiven()) {
			final int fixedDigit = selectedCell.getFixedDigit();
			selectedCell.setCandidatesVisible(true);
			selectedCell.setFixedDigit(Strings.EMPTY);
			this.updateFixedCellTypeCssClass(this.getSelectedCell(), UNFIXED_CELL_CSS_CLASS);
			this.sudokuPuzzleValues.setCellFixedDigit(selectedCell.getRow(), selectedCell.getCol(), 0);
			this.addDigitAsCandidateToSeenCells(fixedDigit);
			this.reapplyActiveFilter();
		}
	}

}
