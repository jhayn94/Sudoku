package sudoku.state.cell;

import javafx.collections.ObservableList;

/**
 * This class corresponds to a sudoku cell which is a given and cannot be
 * changed.
 */
public class GivenSudokuCellState extends DefaultSudokuCellState {

	public GivenSudokuCellState(DefaultSudokuCellState lastState) {
		super(lastState);
		this.onEnter();
	}

	@Override
	protected void onEnter() {
		final ObservableList<String> styleClass = this.cell.getStyleClass();
		styleClass.remove(FIXED_USER_DIGIT_CSS_CLASS);
		styleClass.add(FIXED_GIVEN_DIGIT_CSS_CLASS);
	}
}
