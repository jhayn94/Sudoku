package sudoku.state.model.filter;

import org.apache.logging.log4j.util.Strings;

import sudoku.state.ApplicationModelState;

/**
 * This class updates the state of the application when the user presses a
 * numbered button on the left side of the screen, or F1 - F10. This should
 * annotate the puzzle with green cells where the candidate can go.
 */
public class ApplyFilterState extends ApplicationModelState {

	protected final String newCellFilter;

	public ApplyFilterState(final ApplicationModelState applicationModelState, final String filter) {
		super(applicationModelState, false);
		this.newCellFilter = filter;
	}

	@Override
	public void onEnter() {
		this.updateFilterButtonStates(this.newCellFilter);
		this.resetAllFilters();
		if (this.sudokuPuzzleStyle.getActiveCellFilter().equals(this.newCellFilter)) {
			this.sudokuPuzzleStyle.setActiveCellFilter(Strings.EMPTY);
		} else {
			this.sudokuPuzzleStyle.setActiveCellFilter(this.newCellFilter);
			this.applyActiveFilter();
		}

	}

}
