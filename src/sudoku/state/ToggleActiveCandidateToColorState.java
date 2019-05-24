package sudoku.state;

import javafx.scene.input.KeyCode;
import sudoku.model.SudokuPuzzle;

/**
 * This class contains methods to reply to PAGE UP + PAGE DOWN, which cycle the
 * active candidate digit used when applying colors to candidates.
 */
public class ToggleActiveCandidateToColorState extends ApplicationModelState {

	public ToggleActiveCandidateToColorState(final KeyCode keyCode, final ApplicationModelState lastState) {
		super(lastState);
		this.lastKeyCode = keyCode;
	}

	@Override
	public void onEnter() {
		if (KeyCode.PAGE_UP == this.lastKeyCode) {
			this.activeColorCandidateDigit++;
		} else {
			this.activeColorCandidateDigit--;
		}

		if (this.activeColorCandidateDigit < 1) {
			this.activeColorCandidateDigit = SudokuPuzzle.CELLS_PER_HOUSE;
		} else if (this.activeColorCandidateDigit > SudokuPuzzle.CELLS_PER_HOUSE) {
			this.activeColorCandidateDigit = 1;
		}

		// TODO - once the UI components exist, change those as well.
	}

}
