package sudoku.state.cell;

import org.apache.logging.log4j.util.Strings;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sudoku.view.puzzle.SudokuPuzzleCell;

/** This class corresponds to the default behavior of a sudoku cell. */
public class DefaultSudokuCellState {

	protected static final int NUM_CANDIDATES = 9;

	protected static final String FIXED_USER_DIGIT_CSS_CLASS = "sudoku-fixed-user-digit";

	protected static final String FIXED_GIVEN_DIGIT_CSS_CLASS = "sudoku-fixed-given-digit";

	protected final boolean[] candidatesVisible;

	protected final boolean fixedDigitVisible;

	protected final SudokuPuzzleCell cell;

	public DefaultSudokuCellState(SudokuPuzzleCell cell) {
		this.cell = cell;
		this.candidatesVisible = new boolean[NUM_CANDIDATES];
		for (int i = 0; i < NUM_CANDIDATES; i++) {
			this.candidatesVisible[i] = true;
		}
		this.fixedDigitVisible = false;
	}

	public DefaultSudokuCellState(DefaultSudokuCellState lastState) {
		this.cell = lastState.cell;
		this.candidatesVisible = lastState.candidatesVisible;
		this.fixedDigitVisible = lastState.fixedDigitVisible;
	}

	protected void onEnter() {

	}

	public EventHandler<KeyEvent> handleKeyPress() {
		return event -> {
			final KeyCode code = event.getCode();
			if (code.isDigitKey()) {
				if (event.isControlDown()) {
					final int pressedDigitIndex = Integer.parseInt(code.getName()) - 1;
					final boolean isCandidateVisible = this.candidatesVisible[pressedDigitIndex];
					this.candidatesVisible[pressedDigitIndex] = !isCandidateVisible;
					this.cell.setCandidateVisible(pressedDigitIndex, !isCandidateVisible);
				} else {
					this.cell.setCandidatesVisible(false);
					this.cell.setFixedDigit(code.getName());
				}
			} else if (KeyCode.DELETE == code) {
				this.cell.setCandidatesVisible(true);
				this.cell.setFixedDigit(Strings.EMPTY);
			}
		};
	}
}
