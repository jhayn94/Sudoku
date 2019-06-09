package sudoku.state.model.cell;

import sudoku.core.ViewController;
import sudoku.model.SudokuPuzzleValues;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.puzzle.SudokuPuzzleCell;

/**
 * This class updates the state of the application when cells are set as given.
 * This is done via the context menu.
 */
public class SetGivenCellsState extends ApplicationModelState {

	public SetGivenCellsState(final ApplicationModelState lastState) {
		super(lastState, false);
	}

	@Override
	public void onEnter() {
		ViewController.getInstance().getRootPane().removeAllAnnotations();
		// Don't want the user to be able to undo back to another puzzle.
		this.applicationStateHistory.clearUndoStack();
		this.applicationStateHistory.clearRedoStack();
		this.updateUndoRedoButtons();
		this.setFilledCellsAsGiven();
		this.reapplyActiveFilter();
		this.updateAllPuzzleStatsForNewPuzzle();
	}

	private void setFilledCellsAsGiven() {
		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				final int givenDigit = this.sudokuPuzzleValues.getFixedCellDigit(row, col);
				if (givenDigit != 0) {
					this.sudokuPuzzleValues.setGivenCellDigit(row, col, givenDigit);
					final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
					// They probably aren't visible already, but just in case.
					sudokuPuzzleCell.setCandidatesVisible(false);
					this.updateFixedCellTypeCssClass(sudokuPuzzleCell, GIVEN_CELL_CSS_CLASS);
				}
			}
		}
	}

}
