package sudoku.state;

import sudoku.core.ViewController;
import sudoku.model.SudokuPuzzle;
import sudoku.view.puzzle.SudokuPuzzleCell;

/**
 * This class represents the state of the application on startup.
 */
public class DefaultApplicationModelState extends ApplicationModelState {

	public DefaultApplicationModelState() {
		super();
		// Populates the cell states with the default value for each cell.
		for (int row = 0; row < SudokuPuzzle.NUMBER_OF_CELLS_PER_DIMENSION; row++) {
			for (int col = 0; col < SudokuPuzzle.NUMBER_OF_CELLS_PER_DIMENSION; col++) {
				final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(col, row);
				this.cellActionStates[col][row] = sudokuPuzzleCell.getActionState();
				this.cellActiveStates[col][row] = sudokuPuzzleCell.getActiveState();
			}
		}
		this.onEnter();
	}

	@Override
	protected void onEnter() {
		// Nothing to do.
	}

}