package sudoku.state.model;

import sudoku.view.util.MouseMode;

/**
 * This class updates the state of the application when the user changes the
 * mouse mode.
 */
public class MouseModeChangedState extends ResetFromModelState {

	public MouseModeChangedState(final String newMouseMode, final ApplicationModelState lastState) {
		super(lastState, false);
		this.mouseMode = MouseMode.valueOf(newMouseMode);
	}

	@Override
	public void onEnter() {
		if (MouseMode.SELECT_CELLS != this.mouseMode) {
			this.getSelectedCell().setIsSelected(false);
		}
	}

}
