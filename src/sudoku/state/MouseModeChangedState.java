package sudoku.state;

import sudoku.view.util.MouseMode;

/**
 * This class updates the state of the application when the user invokes an
 * "undo", either through the keyboard or a button press in the UI.
 */
public class MouseModeChangedState extends ResetFromModelState {

	public MouseModeChangedState(final String newMouseMode, final ApplicationModelState lastState) {
		super(lastState, false);
		this.mouseMode = MouseMode.valueOf(newMouseMode);
	}

	@Override
	public void onEnter() {
		// Nothing to do other than the model change in the constructor.
	}

}
