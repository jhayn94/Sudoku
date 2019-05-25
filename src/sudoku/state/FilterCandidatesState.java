package sudoku.state;

import java.util.List;

import org.apache.logging.log4j.util.Strings;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import sudoku.core.ViewController;
import sudoku.view.NumericButtonPane;

/**
 * This state is set when the user clicks a button which should invoke a filter
 * on the SudoKu grid.
 */
public class FilterCandidatesState extends ApplicationModelState {

	private static final String SUDOKU_BUTTON_SELECTED = "sudoku-button-selected";

	private static final String SUDOKU_BUTTON_UNSELECTED = "sudoku-button-unselected";

	private final String newCellFilter;

	public FilterCandidatesState(final ApplicationModelState applicationModelState, final String filter) {
		super(applicationModelState, false);
		this.newCellFilter = filter;
	}

	@Override
	public void onEnter() {
		final NumericButtonPane numericButtonPane = ViewController.getInstance().getNumericButtonPane();
		final List<Button> filterButtons = numericButtonPane.getFilterButtons();
		filterButtons.forEach(this::updateFilterButton);
		if (this.sudokuPuzzleStyle.getActiveCellFilter().equals(this.newCellFilter)) {
			this.sudokuPuzzleStyle.setActiveCellFilter(Strings.EMPTY);
		} else {
			this.sudokuPuzzleStyle.setActiveCellFilter(this.newCellFilter);
		}
	}

	public void updateFilterButton(final Button button) {
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
		return buttonText.equals(this.newCellFilter) && !buttonText.equals(this.sudokuPuzzleStyle.getActiveCellFilter());
	}

}
