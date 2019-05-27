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
	public void onEnter() {
		// There are various other threads that need to be stopped; this configuration
		// seems to do that.
		Platform.runLater(() -> {
			Platform.exit();
			System.exit(0);
		});
	}

}
