package sudoku.state.window;

import javafx.application.Platform;

/**
 * This class contains code to run when the user closes the application.
 */
public class ClosedState extends ApplicationWindowState {

	public ClosedState(final ApplicationWindowState lastState) {
		super(lastState);
	}

	@Override
	protected void onEnter() {
		Platform.runLater(Platform::exit);
	}

}
