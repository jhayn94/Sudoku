package sudoku.state.window;

import javafx.application.Platform;
import sudoku.core.ViewController;

/**
 * This class contains code to run when the user minimizes the application.
 */
public class MinimizedState extends ApplicationWindowState {

	public MinimizedState(final ApplicationWindowState lastState) {
		super(lastState);
	}

	@Override
	protected void onEnter() {
		if (Platform.isFxApplicationThread()) {
			this.doMinimize();
		} else {
			Platform.runLater(this::doMinimize);
		}
	}

	private void doMinimize() {
		ViewController.getInstance().getStage().setIconified(true);
	}
}
