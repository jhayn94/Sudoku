package sudoku.state.model.cell;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.util.Strings;

import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import sudoku.core.ViewController;
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

			if (ApplicationSettings.getInstance().isAutoManageCandidates()) {
				this.updateCandidatesAfterClearingCell(selectedCell);
			}

			// The digit removed cannot possibly be solved anymore, so enable the filter
			// button.
			final List<Button> filterButtons = ViewController.getInstance().getFilterButtonPane().getFilterButtons();
			filterButtons.get(fixedDigit - 1).setDisable(false);
			this.reapplyActiveFilter();
			this.updateRemainingScoreForPuzzle();
		}
	}

	private void updateCandidatesAfterClearingCell(final SudokuPuzzleCell selectedCell) {
		final Set<Integer> candidateDigitsForCell = this.sudokuPuzzleValues.getCandidateDigitsForCell(selectedCell.getRow(),
				selectedCell.getCol());
		for (int candidate = 1; candidate <= SudokuPuzzleValues.CELLS_PER_HOUSE; candidate++) {
			if (!SudokuPuzzleCellUtils.doesCellSeeFixedDigit(selectedCell.getRow(), selectedCell.getCol(), candidate)) {
				selectedCell.setCandidateVisible(candidate, true);
				candidateDigitsForCell.add(candidate);
			}
		}
	}

}
