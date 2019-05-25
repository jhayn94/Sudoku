package sudoku.state;

import org.apache.logging.log4j.util.Strings;

/**
 * This class updates the state of the application when the user presses a
 * numbered button on the left side of the screen, or F1 - F10. This should
 * annotate the puzzle with green cells where the candidate can go.
 */
public class ApplyFilterState extends ApplicationModelState {

	protected final String newCellFilter;

	public ApplyFilterState(final ApplicationModelState applicationModelState, final String filter) {
		super(applicationModelState);
		this.newCellFilter = filter;
	}

	@Override
	public void onEnter() {
		this.updateFilterButtonStates(this.newCellFilter);
		this.resetAllFilters();
		if (this.activeCellFilter.equals(this.newCellFilter)) {
			this.activeCellFilter = Strings.EMPTY;
		} else {
			this.activeCellFilter = this.newCellFilter;
			this.applyActiveFilter();
		}

	}

}
