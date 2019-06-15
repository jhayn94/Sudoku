package sudoku.state.model.filter;

import java.util.List;

import org.apache.logging.log4j.util.Strings;

import javafx.scene.control.Button;
import sudoku.core.ViewController;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.sidebar.FilterButtonPane;
import sudoku.view.util.LabelConstants;

/**
 * This class updates the state of the application when the user presses a
 * numbered button on the left side of the screen, or F1 - F10. This should
 * annotate the puzzle with green cells where the candidate can go.
 */
public class ApplyFilterState extends ApplicationModelState {

	// This is a string because the current filter is stored as a string. Thus, we
	// can work this into the control flow more easily.
	private static final String BIVALUE_CELL_FILTER_INDEX = "10";

	protected final String newCellFilter;

	public ApplyFilterState(final ApplicationModelState applicationModelState, final String filter) {
		super(applicationModelState, false);
		this.newCellFilter = filter;
	}

	@Override
	public void onEnter() {
		final int currentFilterIndex = Integer
				.valueOf(this.newCellFilter.replace(LabelConstants.BIVALUE_CELL, BIVALUE_CELL_FILTER_INDEX));
		final FilterButtonPane filterButtonPane = ViewController.getInstance().getFilterButtonPane();
		final List<Button> filterButtons = filterButtonPane.getFilterButtons();
		// No effect if the button is disabled.
		if (filterButtons.get(currentFilterIndex - 1).isDisabled()) {
			return;
		}
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
