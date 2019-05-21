package sudoku.state;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

	public FilterCandidatesState(ApplicationModelState applicationModelState, String filter) {
		super(applicationModelState);
		this.activeCellFilter = filter;
		this.onEnter();
	}

	@Override
	protected void onEnter() {
		final NumericButtonPane numericButtonPane = ViewController.getInstance().getNumericButtonPane();
		final List<Button> filterButtons = numericButtonPane.getFilterButtons();
		filterButtons.forEach(this::updateFilterButton);
	}

	public void updateFilterButton(Button button) {
		String cssStyling = button.getStyle();
		if (button.getText().contentEquals(this.activeCellFilter)) {
			cssStyling += SUDOKU_BUTTON_SELECTED;
		} else {
			cssStyling += SUDOKU_BUTTON_UNSELECTED;
		}
		button.setStyle(cssStyling);
	}

}
