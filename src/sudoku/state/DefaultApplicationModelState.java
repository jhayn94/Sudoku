package sudoku.state;

import sudoku.core.ViewController;
import sudoku.view.puzzle.SudokuPuzzleCell;

/**
 * This class updates the state of the application on startup. It is the default
 * value for the model state. Some initial values are set here that would not be
 * considered default values for fields.
 */
public class DefaultApplicationModelState extends ApplicationModelState {

	private static final int MIDDLE_CELL_INDEX = 4;

	public DefaultApplicationModelState() {
		super();
		this.selectedCellRow = MIDDLE_CELL_INDEX;
		this.selectedCellCol = MIDDLE_CELL_INDEX;
		final SudokuPuzzleCell middleCell = ViewController.getInstance().getSudokuPuzzleCell(4, 4);
		this.getSelectedCell().setIsSelected(true);
		this.onEnter();
	}

	@Override
	public void onEnter() {
		// Nothing to do.
	}

}