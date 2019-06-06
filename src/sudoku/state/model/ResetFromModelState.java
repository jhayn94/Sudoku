package sudoku.state.model;

import java.util.List;
import java.util.stream.IntStream;

import org.apache.logging.log4j.util.Strings;

import sudoku.core.ViewController;
import sudoku.model.SudokuPuzzleValues;
import sudoku.view.hint.HintTextArea;
import sudoku.view.puzzle.SudokuPuzzleCell;

/**
 * This class contains methods to reset the application according to the current
 * sudoku puzzle set. This is used mainly as a parent class for undo + redo
 * states.
 */
public abstract class ResetFromModelState extends ApplicationModelState {

	protected ResetFromModelState(final ApplicationModelState lastState, final boolean addToHistory) {
		super(lastState, false);
	}

	/**
	 * Uses the value of SudokuPuzzleValues in this.sudokuPuzzleValues, and resets
	 * the rest of the model + view to match it. This is used for redo, undo and
	 * restart.
	 */
	protected void resetApplicationFromPuzzleState() {
		this.updateCells();
		// Must do candidate updates after because the cell values need to be finished
		// before setting candidates. Otherwise the doesCellSeeFixedDigit checks will
		// not be correct.
		this.updateCandidates();
		this.reapplyActiveFilter();
		this.updateRemainingScoreForPuzzle();
		final HintTextArea hintTextArea = ViewController.getInstance().getHintTextArea();
		hintTextArea.getHintTextArea().setText(Strings.EMPTY);
	}

	/**
	 * Updates the cells (the view) to match the model.
	 */
	protected void updateCells() {
		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				final int fixedCellDigit = this.sudokuPuzzleValues.getFixedCellDigit(row, col);
				final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
				if (fixedCellDigit != 0) {
					this.updateFixedCell(row, col, fixedCellDigit, sudokuPuzzleCell);
				} else {
					this.updateUnfixedCell(row, col, sudokuPuzzleCell);
				}
				final List<Integer> candidateDigitsForCell = this.sudokuPuzzleValues.getCandidateDigitsForCell(row, col);
				IntStream.rangeClosed(1, SudokuPuzzleValues.CELLS_PER_HOUSE).forEach(digit -> {
					sudokuPuzzleCell.setCandidateVisible(digit, candidateDigitsForCell.contains(digit));
				});

			}
		}
	}

	private void updateFixedCell(final int row, final int col, final int fixedCellDigit,
			final SudokuPuzzleCell sudokuPuzzleCell) {
		sudokuPuzzleCell.setCandidatesVisible(false);
		sudokuPuzzleCell.setFixedDigit(String.valueOf(fixedCellDigit));
		final int givenCellDigit = this.sudokuPuzzleValues.getGivenCellDigit(row, col);
		this.updateFixedCellTypeCssClass(sudokuPuzzleCell,
				givenCellDigit == 0 ? FIXED_CELL_CSS_CLASS : GIVEN_CELL_CSS_CLASS);
	}

	private void updateUnfixedCell(final int row, final int col, final SudokuPuzzleCell sudokuPuzzleCell) {
		this.sudokuPuzzleValues.setCellFixedDigit(row, col, 0);
		sudokuPuzzleCell.setCandidatesVisible(true);
		sudokuPuzzleCell.setFixedDigit(Strings.EMPTY);
		this.updateFixedCellTypeCssClass(sudokuPuzzleCell, UNFIXED_CELL_CSS_CLASS);
	}
}
