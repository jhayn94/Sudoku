package sudoku.state.model.candidate;

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

	public ToggleActiveCandidateState(final KeyCode keyCode, final ApplicationModelState lastState) {
		super(lastState, false);
		this.lastKeyCode = keyCode;
	}

	@Override
	public void onEnter() {
		int activeColorCandidateDigit = this.sudokuPuzzleStyle.getActiveCandidateDigit();
		if (KeyCode.PAGE_UP == this.lastKeyCode) {
			activeColorCandidateDigit++;
		} else {
			activeColorCandidateDigit--;
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
