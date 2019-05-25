package sudoku.state;

/**
 * This class updates the state of the application when the user invokes a
 * "redo", either through the keyboard or a button press in the UI.
 */
public class RedoActionState extends ApplicationModelState {

	public RedoActionState(final ApplicationModelState lastState) {
		super(lastState);
	}

	@Override
	public void onEnter() {
	}

}
