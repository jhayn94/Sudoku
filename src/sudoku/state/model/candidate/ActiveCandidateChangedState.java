package sudoku.state.model.candidate;

import org.apache.logging.log4j.util.Strings;

import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import sudoku.core.ViewController;
import sudoku.model.SudokuPuzzleValues;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.sidebar.ControlHelperPane;

/**
 * This class updates the state of the application contains methods to change
 * active candidate, which is used when applying colors to candidates, or
 * toggling candidate visibility with the mouse.
 */
public class ActiveCandidateChangedState extends ApplicationModelState {

	private static final String DIGIT_REPLACE_TEXT = "DIGIT";

	private static final String NUMPAD_REPLACE_TEXT = "NUMPAD";

	public ActiveCandidateChangedState(final KeyCode keyCode, final ApplicationModelState lastState) {
		super(lastState, false);
		this.lastKeyCode = keyCode;
	}

	@Override
	public void onEnter() {
		int activeColorCandidateDigit = this.sudokuPuzzleStyle.getActiveCandidateDigit();
		final ControlHelperPane controlHelperPane = ViewController.getInstance().getControlHelperPane();
		final Button oldActiveDigitButton = controlHelperPane.getDigitButton(activeColorCandidateDigit - 1);
		oldActiveDigitButton.getStyleClass().remove(SUDOKU_COMBO_BUTTON_SELECTED_CSS_CLASS);
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
		this.updateRemainingScoreForPuzzle();
		final Button newActiveDigitButton = controlHelperPane.getDigitButton(activeColorCandidateDigit - 1);
		newActiveDigitButton.getStyleClass().add(SUDOKU_COMBO_BUTTON_SELECTED_CSS_CLASS);
	}

}
