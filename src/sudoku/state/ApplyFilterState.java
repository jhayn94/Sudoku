package sudoku.state;

import java.util.List;

import org.apache.logging.log4j.util.Strings;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import sudoku.core.ViewController;
import sudoku.view.NumericButtonPane;

/**
 * This class updates the state of the application when the user presses a
 * numbered button on the left side of the screen. This should annotate the
 * puzzle with green cells where the candidate can go.
 */
public class ApplyFilterState extends ApplicationModelState {

	private static final String SUDOKU_BUTTON_SELECTED = "sudoku-button-selected";

	private static final String SUDOKU_BUTTON_UNSELECTED = "sudoku-button-unselected";

	private final String newCellFilter;

	public ApplyFilterState(final ApplicationModelState applicationModelState, final String filter) {
		super(applicationModelState);
		this.newCellFilter = filter;
	}

	@Override
	public void onEnter() {
		final NumericButtonPane numericButtonPane = ViewController.getInstance().getNumericButtonPane();
		final List<Button> filterButtons = numericButtonPane.getFilterButtons();
		filterButtons.forEach(this::updateFilterButton);
		this.resetAllFilters();
		if (this.activeCellFilter.equals(this.newCellFilter)) {
			this.activeCellFilter = Strings.EMPTY;
		} else {
			this.activeCellFilter = this.newCellFilter;
			this.applyActiveFilter();
		}

	}

	private void updateFilterButton(final Button button) {
		final ObservableList<String> styleClass = button.getStyleClass();
		// Since we iterate over every button every time, the classes are fully
		// cleared to avoid duplicate classes. This is easier than tracking when to
		// remove each CSS class separately.
		styleClass.remove(SUDOKU_BUTTON_SELECTED);
		styleClass.remove(SUDOKU_BUTTON_UNSELECTED);
		if (!this.shouldSetButtonSelected(button)) {
			styleClass.add(SUDOKU_BUTTON_UNSELECTED);
		} else {
			styleClass.add(SUDOKU_BUTTON_SELECTED);
		}
	}

	private boolean shouldSetButtonSelected(final Button button) {
		final String buttonText = button.getText();
		return buttonText.equals(this.newCellFilter) && !buttonText.equals(this.activeCellFilter);
	}

}
