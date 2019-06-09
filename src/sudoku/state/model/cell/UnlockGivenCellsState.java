package sudoku.state.model.cell;

import org.apache.logging.log4j.util.Strings;

import sudoku.core.ViewController;
import sudoku.model.SudokuPuzzleValues;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.puzzle.SudokuPuzzleCell;
import sudoku.view.sidebar.PuzzleStatsPane;

/**
 * This class updates the state of the application when cells are unlocked from
 * a given state. This is done via the context menu. Its main use is if you made
 * a mistake while setting your givens.
 */
public class UnlockGivenCellsState extends ApplicationModelState {

	public UnlockGivenCellsState(final ApplicationModelState lastState) {
		super(lastState, false);
	}

	@Override
	public void onEnter() {
		ViewController.getInstance().getRootPane().removeAllAnnotations();
		// Not really sure if it makes sense to allow undo, so just clear the stacks to
		// be safe.
		this.applicationStateHistory.clearUndoStack();
		this.applicationStateHistory.clearRedoStack();
		this.updateUndoRedoButtons();
		this.sudokuPuzzleValues.setHasGivens(false);
		this.setFilledCellsAsNotGiven();

		final PuzzleStatsPane puzzleStatsPane = ViewController.getInstance().getPuzzleStatsPane();
		puzzleStatsPane.getDifficultyTextField().setText(Strings.EMPTY);
		puzzleStatsPane.getRatingTextField().setText(Strings.EMPTY);
		puzzleStatsPane.getRemainingRatingTextField().setText(Strings.EMPTY);
	}

	private void setFilledCellsAsNotGiven() {
		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				final int givenDigit = this.sudokuPuzzleValues.getFixedCellDigit(row, col);
				if (givenDigit != 0) {
					this.sudokuPuzzleValues.setGivenCellDigit(row, col, 0);
					final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
					// They probably aren't visible already, but just in case.
					sudokuPuzzleCell.setCandidatesVisible(false);
					this.updateFixedCellTypeCssClass(sudokuPuzzleCell, FIXED_CELL_CSS_CLASS);
				}
			}
		}
	}

}
