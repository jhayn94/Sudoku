package sudoku.state.model;

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
		// before
		// setting candidates. Otherwise the doesCellSeeFixedDigit checks will not be
		// correct.
		this.updateCandidates();
		this.resetColorStates();
		this.reapplyActiveFilter();
		this.updateAllPuzzleStatsForNewPuzzle();
	}
}
