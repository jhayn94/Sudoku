package sudoku.state.model.filter;

import java.util.List;

import org.apache.logging.log4j.util.Strings;

import javafx.scene.control.Button;
import sudoku.core.ViewController;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.sidebar.FilterButtonPane;
import sudoku.view.util.LabelConstants;

/**
 * This class updates the state of the application when the user presses the
 * COMMA or PERIOD keys. This should cycle the active input. PERIOD cycles
 * upward ( 1 -> 9), and COMMA cycles downward (9 -> 1).
 */

public class CycleActiveFilterState extends ApplicationModelState {

	// This is a string because the current filter is stored as a string. Thus, we
	// can work this into the control flow more easily.
	private static final String BIVALUE_CELL_FILTER_INDEX = "10";

	// KeyCode.PERIOD.getName()
	private static final String PERIOD = "Period";

	private static final int NUM_FILTERS = 10;

	private final String newCellFilter;

	public CycleActiveFilterState(final ApplicationModelState applicationModelState, final String filter) {
		super(applicationModelState, false);
		if (!this.sudokuPuzzleStyle.getActiveCellFilter().isEmpty()) {
			this.newCellFilter = this.parseCycleFilterInput(filter);
		} else {
			this.newCellFilter = Strings.EMPTY;
		}
	}

	@Override
	public void onEnter() {
		// No active filter means these buttons should have no effect.
		if (this.sudokuPuzzleStyle.getActiveCellFilter().isEmpty()) {
			return;
		}

		this.updateFilterButtonStates(this.newCellFilter);
		this.resetAllFilters();

		this.sudokuPuzzleStyle.setActiveCellFilter(this.newCellFilter);
		this.applyActiveFilter();

	}

	/**
	 * Takes the raw input to the state (the key pressed), and translates that to
	 * which filter should be active.
	 *
	 */
	private String parseCycleFilterInput(final String filterInput) {
		// Note that F10 is for bivalue cells, F1 - F9 are for the corresponding digit.
		final int currentFilterIndex = Integer.valueOf(
				this.sudokuPuzzleStyle.getActiveCellFilter().replace(LabelConstants.BIVALUE_CELL, BIVALUE_CELL_FILTER_INDEX));
		int newFilterDigit = currentFilterIndex;

		final FilterButtonPane filterButtonPane = ViewController.getInstance().getFilterButtonPane();
		final List<Button> filterButtons = filterButtonPane.getFilterButtons();
		// Skip over a filter if a digit is entirely solved.
		while (newFilterDigit == currentFilterIndex || filterButtons.get(newFilterDigit - 1).isDisabled()) {
			if (PERIOD.equals(filterInput)) {
				newFilterDigit = newFilterDigit + 1;
			} else {
				newFilterDigit = newFilterDigit - 1;
			}
			// It would be nice to use % instead, but since the range is [1, 10], and not
			// [0, 9], it won't work.
			if (newFilterDigit == 0) {
				newFilterDigit = 10;
			} else if (newFilterDigit == NUM_FILTERS + 1) {
				newFilterDigit = 1;
			}
		}
		return String.valueOf(newFilterDigit).replace(BIVALUE_CELL_FILTER_INDEX, LabelConstants.BIVALUE_CELL);
	}

}
