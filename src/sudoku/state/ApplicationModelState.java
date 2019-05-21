package sudoku.state;

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

	/** Constructor for the initialization of the application. */
	protected ApplicationModelState() {
		this.activeCellFilter = "";
		this.filterAllowedCells = false;
	}

	/** Constructor for state transitions. */
	protected ApplicationModelState(final ApplicationModelState lastState) {
		// TODO - assign lastState components to this.
		this.activeCellFilter = lastState.activeCellFilter;
		this.filterAllowedCells = lastState.filterAllowedCells;
	}

	protected abstract void onEnter();
}
