package sudoku.state.model.cell;

import org.apache.logging.log4j.util.Strings;

import javafx.scene.input.KeyCode;
import sudoku.model.ApplicationSettings;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.puzzle.SudokuPuzzleCell;

/**
 * This class updates the state of the application when a cell's digit is set.
 */
public class SetDigitState extends ApplicationModelState {

	private static final String DIGIT_REPLACE_TEXT = "DIGIT";

	private static final String NUMPAD_REPLACE_TEXT = "NUMPAD";

	public SetDigitState(final KeyCode keyCode, final ApplicationModelState lastState) {
		super(lastState, true);
		this.lastKeyCode = keyCode;
	}

	@Override
	public void onEnter() {
		final int selectedCellRow = this.sudokuPuzzleStyle.getSelectedCellRow();
		final int selectedCellCol = this.sudokuPuzzleStyle.getSelectedCellCol();
		if (selectedCellRow != -1 && selectedCellCol != -1) {
			final SudokuPuzzleCell selectedCell = this.getSelectedCell();
			if (this.sudokuPuzzleValues.getGivenCellDigit(selectedCellRow, selectedCellCol) == 0) {
				final int oldFixedDigit = selectedCell.getFixedDigit();

				selectedCell.setCandidatesVisible(false);
				selectedCell.setFixedDigit(this.lastKeyCode.toString());
				this.updateFixedCellTypeCssClass(this.getSelectedCell(), FIXED_CELL_CSS_CLASS);

				final int digit = Integer.parseInt(this.lastKeyCode.toString().replace(DIGIT_REPLACE_TEXT, Strings.EMPTY)
						.replace(NUMPAD_REPLACE_TEXT, Strings.EMPTY));
				this.sudokuPuzzleValues.setCellFixedDigit(selectedCell.getRow(), selectedCell.getCol(), digit);
				this.updateCandidates(selectedCell, oldFixedDigit);
				this.updateFilterButtonEnabled(digit);
				this.reapplyActiveFilter();
				this.updateRemainingScoreForPuzzle();
			}
		}
	}

	private void updateCandidates(final SudokuPuzzleCell selectedCell, final int oldFixedDigit) {
		if (ApplicationSettings.getInstance().isAutoManageCandidates()) {
			if (oldFixedDigit != -1) {
				this.addDigitAsCandidateToSeenCells(oldFixedDigit);
			}
			this.removeImpermissibleCandidates(selectedCell);
		}
	}

}
