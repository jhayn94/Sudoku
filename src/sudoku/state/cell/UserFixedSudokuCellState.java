package sudoku.state.cell;

import org.apache.logging.log4j.util.Strings;

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
		this.updateCssClass(FIXED_USER_DIGIT_CSS_CLASS);
	}

	@Override
	public EventHandler<KeyEvent> handleKeyPress() {
		return event -> {
			final KeyCode code = event.getCode();
			if (KeyCode.DELETE == code) {
				this.cell.setCandidatesVisible(true);
				this.cell.setFixedDigit(Strings.EMPTY);
				this.cell.setState(new DefaultSudokuCellState(this));
			}
		};
	}

}
