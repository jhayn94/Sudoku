package sudoku.state;

import sudoku.factories.ModelFactory;
import sudoku.model.SudokuPuzzle;
import sudoku.state.cell.DefaultSudokuCellState;

/**
 * This class is a representation of the current state of the application model,
 * with methods to invoke when a state change occurs.
 */
public abstract class ApplicationModelState {

	// The active cell filter, or empty string for none.
	protected String activeCellFilter;

	// True if a filter should show the permitted cells for the active filter (if
	// any), false it should show the disallowed cells.
	protected final boolean filterAllowedCells;

	protected final DefaultSudokuCellState[][] cellStates;

	protected final SudokuPuzzle puzzleModel;

	protected int selectedCellRow;
	protected int selectedCellCol;

	/** Constructor for the initialization of the application. */
	protected ApplicationModelState() {
		this.activeCellFilter = "";
		this.filterAllowedCells = false;
		this.cellStates = new DefaultSudokuCellState[SudokuPuzzle.NUMBER_OF_CELLS_PER_DIMENSION][SudokuPuzzle.NUMBER_OF_CELLS_PER_DIMENSION];
		this.puzzleModel = ModelFactory.getInstance().createSudokuPuzzle();
		this.selectedCellRow = -1;
		this.selectedCellCol = -1;

	}

	/** Constructor for state transitions. */
	protected ApplicationModelState(final ApplicationModelState lastState) {
		this.activeCellFilter = lastState.activeCellFilter;
		this.filterAllowedCells = lastState.filterAllowedCells;
		this.cellStates = lastState.cellStates;
		this.puzzleModel = lastState.puzzleModel;
		this.selectedCellRow = lastState.selectedCellRow;
		this.selectedCellCol = lastState.selectedCellCol;

	}

	protected abstract void onEnter();
}
