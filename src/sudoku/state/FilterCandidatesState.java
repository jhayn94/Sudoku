package sudoku.state;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import javafx.scene.control.Button;
import sudoku.core.ViewController;
import sudoku.view.NumericButtonPane;

/**
 * This state is set when the user clicks a button which should invoke a filter
 * on the SudoKu grid.
 */
public class FilterCandidatesState extends ApplicationModelState {

	private static final String SUDOKU_BUTTON_SELECTED = "-fx-background-color: -sudoku-color-stone-blue;"
			+ "	-fx-text-fill: -sudoku-color-parchment;";
	private static final String SUDOKU_BUTTON_UNSELECTED = "-fx-background-color: -sudoku-color-parchment;"
			+ "	-fx-text-fill: -sudoku-color-bark;";

	private static final Logger LOG = LogManager.getLogger(FilterCandidatesState.class);
	private final String newCellFilter;

	public FilterCandidatesState(ApplicationModelState applicationModelState, String filter) {
		super(applicationModelState);
		this.newCellFilter = filter;
		this.onEnter();
	}

	@Override
	protected void onEnter() {
		final NumericButtonPane numericButtonPane = ViewController.getInstance().getNumericButtonPane();
		final List<Button> filterButtons = numericButtonPane.getFilterButtons();
		filterButtons.forEach(this::updateFilterButton);
		if (this.activeCellFilter.equals(this.newCellFilter)) {
			this.activeCellFilter = Strings.EMPTY;
		} else {
			this.activeCellFilter = this.newCellFilter;
		}
	}

	public void updateFilterButton(Button button) {
		String cssStyling = button.getStyle();
		if (!this.shouldSetButtonSelected(button)) {
			cssStyling += SUDOKU_BUTTON_UNSELECTED;
		} else {
			cssStyling += SUDOKU_BUTTON_SELECTED;
		}
		button.setStyle(cssStyling);
	}

	private boolean shouldSetButtonSelected(Button button) {
		final String buttonText = button.getText();
		return buttonText.equals(this.newCellFilter) && !buttonText.equals(this.activeCellFilter);
	}

}
