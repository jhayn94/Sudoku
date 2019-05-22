package sudoku.state.cell;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * This class corresponds to a sudoku cell which is set by the user. Candidates
 * cannot be toggled, but the fixed digit may be changed or deleted.
 */
public class UserFixedSudokuCellState extends DefaultSudokuCellState {

	public UserFixedSudokuCellState(DefaultSudokuCellState lastState) {
		super(lastState);
	}

	@Override
	protected void onEnter() {
		this.cell.getStyleClass().remove(SELECTED_CELL_CSS_CLASS);
		this.updateCssClass(FIXED_CELL_CSS_CLASS);
	}

	@Override
	public EventHandler<KeyEvent> handleKeyPressed() {
		return event -> {
			final KeyCode code = event.getCode();
			if (code.isDigitKey() && !event.isControlDown()) {
				this.getCell().setCandidatesVisible(false);
				this.getCell().setFixedDigit(code.getName());
				this.getCell().setState(new UserFixedSudokuCellState(this));
			} else if (KeyCode.DELETE == code) {
				this.handleDeletePressed();
			}
		};
	}

}
