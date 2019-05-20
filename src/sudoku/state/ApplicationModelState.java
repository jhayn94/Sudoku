package sudoku.state;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is a representation of the current state of the application model,
 * with methods to invoke when a state change occurs.
 */
public abstract class ApplicationModelState {

	private static final Logger LOG = LogManager.getLogger(ApplicationModelState.class);

	// A variety of key model components, in the order they would usually be
	// created in the normal workflow.

	/** Constructor for the initialization of the application. */
	protected ApplicationModelState() {
		// TODO - define any model components.
	}

	/** Constructor for state transitions. */
	protected ApplicationModelState(final ApplicationModelState lastState) {
		// TODO - assign lastState components to this.
		this.onEnter();
	}

	protected abstract void onEnter();
}
