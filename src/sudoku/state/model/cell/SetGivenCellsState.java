package sudoku.state.model.cell;

import sudoku.core.ViewController;
import sudoku.model.SudokuPuzzleValues;
import sudoku.state.ApplicationModelState;
import sudoku.view.puzzle.SudokuPuzzleCell;

/**
 * This class updates the state of the application when cells are set as given.
 * TODO - does this need to be a state? Should it be part of a "new puzzle"
 * state?
 */
public class SetGivenCellsState extends ApplicationModelState {

	public SetGivenCellsState(final ApplicationModelState lastState) {
		super(lastState, false);
		// Don't want the user to be able to undo back to another puzzle.
		this.applicationStateHistory.clearRedoStack();
	}

	@Override
	public void onEnter() {

		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				final int givenDigit = this.sudokuPuzzleValues.getFixedCellDigit(row, col);
				if (givenDigit != 0) {
					this.sudokuPuzzleValues.setGivenCellDigit(row, col, givenDigit);
					final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
					sudokuPuzzleCell.setCellGiven(true);
					// They probably aren't visible already, but just in case.
					sudokuPuzzleCell.setCandidatesVisible(false);
					this.updateFixedCellTypeCssClass(sudokuPuzzleCell, GIVEN_CELL_CSS_CLASS);
				}
			}
		}
		this.reapplyActiveFilter();
	}

}
