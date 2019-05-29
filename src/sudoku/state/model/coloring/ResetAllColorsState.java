package sudoku.state.model.coloring;

import javafx.scene.input.KeyCode;
import sudoku.state.model.ApplicationModelState;

/**
 * This class updates the state of the application to reply to a R or a CTRL + R
 * key press. This should clear all colored entities, depending on the other
 * input keys.
 */
public class ResetAllColorsState extends ApplicationModelState {

	public ResetAllColorsState(final ApplicationModelState lastState) {
		super(lastState, false);
		this.lastKeyCode = KeyCode.R;
	}

	@Override
	public void onEnter() {
		this.resetColorStates();
	}

}
