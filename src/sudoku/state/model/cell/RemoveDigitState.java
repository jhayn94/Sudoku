package sudoku.state.model.cell;

import java.util.List;

import org.apache.logging.log4j.util.Strings;

import javafx.scene.input.KeyCode;
import sudoku.model.ApplicationSettings;
import sudoku.model.SudokuPuzzleValues;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.puzzle.SudokuPuzzleCell;
import sudoku.view.puzzle.SudokuPuzzleCellUtils;

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
		if (this.sudokuPuzzleValues.getGivenCellDigit(selectedCell.getRow(), selectedCell.getCol()) == 0) {
			final int fixedDigit = selectedCell.getFixedDigit();
			selectedCell.setCandidatesVisible(true);
			selectedCell.setFixedDigit(Strings.EMPTY);
			this.updateFixedCellTypeCssClass(this.getSelectedCell(), UNFIXED_CELL_CSS_CLASS);
			this.sudokuPuzzleValues.setCellFixedDigit(selectedCell.getRow(), selectedCell.getCol(), 0);
			if (ApplicationSettings.getInstance().isAutoManageCandidates()) {
				this.addDigitAsCandidateToSeenCells(fixedDigit);
			}

			final List<Integer> candidateDigitsForCell = this.sudokuPuzzleValues
					.getCandidateDigitsForCell(selectedCell.getRow(), selectedCell.getCol());
			if (ApplicationSettings.getInstance().isAutoManageCandidates()) {
				for (int candidate = 1; candidate <= SudokuPuzzleValues.CELLS_PER_HOUSE; candidate++) {
					if (!SudokuPuzzleCellUtils.doesCellSeeFixedDigit(selectedCell.getRow(), selectedCell.getCol(), candidate)) {
						selectedCell.setCandidateVisible(candidate, true);
						candidateDigitsForCell.add(candidate);
					}
				}
			}
			this.reapplyActiveFilter();
			this.updateRemainingScoreForPuzzle();
		}
	}

}
