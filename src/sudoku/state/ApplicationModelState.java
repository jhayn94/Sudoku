package sudoku.state;

import sudoku.factories.ModelFactory;
import sudoku.model.SudokuPuzzle;
import sudoku.state.cell.action.DefaultCellActionState;
import sudoku.state.cell.active.DefaultCellActiveState;

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

	protected final DefaultCellActionState[][] cellActionStates;

	protected final DefaultCellActiveState[][] cellActiveStates;

	protected final SudokuPuzzle puzzleModel;

	protected int selectedCellRow;
	protected int selectedCellCol;

	/** Constructor for the initialization of the application. */
	protected ApplicationModelState() {
		this.activeCellFilter = "";
		this.filterAllowedCells = false;
		this.cellActionStates = new DefaultCellActionState[SudokuPuzzle.NUMBER_OF_CELLS_PER_DIMENSION][SudokuPuzzle.NUMBER_OF_CELLS_PER_DIMENSION];
		this.cellActiveStates = new DefaultCellActiveState[SudokuPuzzle.NUMBER_OF_CELLS_PER_DIMENSION][SudokuPuzzle.NUMBER_OF_CELLS_PER_DIMENSION];
		this.puzzleModel = ModelFactory.getInstance().createSudokuPuzzle();
		this.selectedCellRow = -1;
		this.selectedCellCol = -1;

	}

	/** Constructor for state transitions. */
	protected ApplicationModelState(final ApplicationModelState lastState) {
		this.activeCellFilter = lastState.activeCellFilter;
		this.filterAllowedCells = lastState.filterAllowedCells;
		this.cellActionStates = lastState.cellActionStates;
		this.cellActiveStates = lastState.cellActiveStates;
		this.puzzleModel = lastState.puzzleModel;
		this.selectedCellRow = lastState.selectedCellRow;
		this.selectedCellCol = lastState.selectedCellCol;

	}

	public abstract void onEnter();
}
