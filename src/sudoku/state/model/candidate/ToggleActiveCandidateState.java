package sudoku.state.model.candidate;

import org.apache.logging.log4j.util.Strings;

import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import sudoku.core.ViewController;
import sudoku.model.SudokuPuzzleValues;
import sudoku.state.model.ApplicationModelState;

/**
 * This class updates the state of the application contains methods to change
 * active candidate, which is used when applying colors to candidates, or
 * toggling candidate visibility with the mouse.
 */
public class ToggleActiveCandidateState extends ApplicationModelState {

	private static final String DIGIT_REPLACE_TEXT = "DIGIT";

	private static final String NUMPAD_REPLACE_TEXT = "NUMPAD";

	public ToggleActiveCandidateState(final KeyCode keyCode, final ApplicationModelState lastState) {
		super(lastState, false);
		this.lastKeyCode = keyCode;
	}

	@Override
	public void onEnter() {
		int activeColorCandidateDigit = this.sudokuPuzzleStyle.getActiveCandidateDigit();
		if (KeyCode.EQUALS == this.lastKeyCode) {
			activeColorCandidateDigit++;
		} else if (KeyCode.MINUS == this.lastKeyCode) {
			activeColorCandidateDigit--;
		} else {
			activeColorCandidateDigit = Integer.parseInt(this.lastKeyCode.getName().replace(DIGIT_REPLACE_TEXT, Strings.EMPTY)
					.replace(NUMPAD_REPLACE_TEXT, Strings.EMPTY));
		}

		if (activeColorCandidateDigit < 1) {
			activeColorCandidateDigit = SudokuPuzzleValues.CELLS_PER_HOUSE;
		} else if (activeColorCandidateDigit > SudokuPuzzleValues.CELLS_PER_HOUSE) {
			activeColorCandidateDigit = 1;
		}
		this.sudokuPuzzleStyle.setActiveCandidateDigit(activeColorCandidateDigit);

		final Label activeColoringCandidateTextArea = ViewController.getInstance().getActiveColoringCandidateLabel();
		activeColoringCandidateTextArea.setText(String.valueOf(activeColorCandidateDigit));

		this.updateRemainingScoreForPuzzle();
	}

}
