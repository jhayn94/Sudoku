package sudoku.state.cell;

import javafx.collections.ObservableList;

/**
 * This class corresponds to a sudoku cell which is set by the user. Candidates
 * cannot be toggled, but the fixed digit may be changed or deleted.
 */
public class UserFixedSudokuCellState extends DefaultSudokuCellState {

	public UserFixedSudokuCellState(DefaultSudokuCellState lastState) {
		super(lastState);
		this.onEnter();
	}

	@Override
	protected void onEnter() {
		final ObservableList<String> styleClass = this.cell.getStyleClass();
		styleClass.remove(FIXED_GIVEN_DIGIT_CSS_CLASS);
		styleClass.add(FIXED_USER_DIGIT_CSS_CLASS);
	}
}
