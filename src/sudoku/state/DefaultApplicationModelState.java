package sudoku.state;

/**
 * This class represents the state of the application on startup.
 */
public class DefaultApplicationModelState extends ApplicationModelState {

	public DefaultApplicationModelState() {
		super();
		this.onEnter();
	}

	@Override
	protected void onEnter() {
		// Nothing to do.
	}

}