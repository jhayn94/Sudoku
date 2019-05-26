package sudoku.state;

import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import sudoku.core.ViewController;
import sudoku.model.SudokuPuzzleValues;

/**
 * This class updates the state of the application contains methods to reply to
 * PAGE UP + PAGE DOWN key presses, which cycle the active candidate digit used
 * when applying colors to candidates.
 */
public class ToggleActiveCandidateToColorState extends ApplicationModelState {

	public ToggleActiveCandidateToColorState(final KeyCode keyCode, final ApplicationModelState lastState) {
		super(lastState, false);
		this.lastKeyCode = keyCode;
	}

	@Override
	public void onEnter() {
		int activeColorCandidateDigit = this.sudokuPuzzleStyle.getActiveColorCandidateDigit();
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
		this.sudokuPuzzleStyle.setActiveColorCandidateDigit(activeColorCandidateDigit);

		final TextArea activeColoringCandidateTextArea = ViewController.getInstance().getActiveColoringCandidateTextArea();
		activeColoringCandidateTextArea.setText(String.valueOf(activeColorCandidateDigit));
	}

}
