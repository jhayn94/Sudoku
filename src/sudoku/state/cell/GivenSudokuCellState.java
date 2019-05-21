package sudoku.state.cell;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

/**
 * This class corresponds to a sudoku cell which is a given and cannot be
 * changed.
 */
public class GivenSudokuCellState extends DefaultSudokuCellState {

	public GivenSudokuCellState(DefaultSudokuCellState lastState) {
		super(lastState);
	}

	@Override
	protected void onEnter() {
		this.updateCssClass(FIXED_GIVEN_DIGIT_CSS_CLASS);
	}

	@Override
	public EventHandler<KeyEvent> handleKeyPress() {
		return event -> {
			// Nothing to do, for now.
		};
	}
}
