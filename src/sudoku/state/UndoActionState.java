package sudoku.state;

/**
 * This class updates the state of the application when the user invokes an
 * "undo", either through the keyboard or a button press in the UI.
 */
public class UndoActionState extends ApplicationModelState {

	public UndoActionState(final ApplicationModelState lastState) {
		super(lastState);
	}

	@Override
	public void onEnter() {
	}

}
