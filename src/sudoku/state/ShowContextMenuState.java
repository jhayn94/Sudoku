package sudoku.state;

import sudoku.core.ViewController;

/**
 * This class contains methods to show or hide the application context menu.
 */
public class ShowContextMenuState extends ApplicationModelState {

	public ShowContextMenuState(final ApplicationModelState lastState) {
		super(lastState, false);
	}

	@Override
	public void onEnter() {
		ViewController.getInstance().getContextMenuButton().toggleContextMenu();
		// No model changes needed.
	}

}
