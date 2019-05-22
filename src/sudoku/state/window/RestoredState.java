package sudoku.state.window;

import javafx.stage.Stage;
import sudoku.core.ViewController;
import sudoku.view.util.ResourceConstants;

/**
 * This class contains code to run when the user restores the application size
 * (from maximized).
 */
public class RestoredState extends ApplicationWindowState {

	public RestoredState(final ApplicationWindowState lastState) {
		super(lastState);
	}

	@Override
	public void onEnter() {
		final Stage stage = ViewController.getInstance().getStage();
		stage.setMaximized(false);
		this.restoreSavedBounds(stage);
		this.setIcon(ViewController.getInstance().getMaximizeWindowButton(), ResourceConstants.MAXIMIZE_ICON);

	}

}
