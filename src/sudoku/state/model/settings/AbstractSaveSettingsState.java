package sudoku.state.model.settings;

import sudoku.model.ApplicationSettings;
import sudoku.state.model.ApplicationModelState;

/**
 * This class updates the state of the application when the user clicks save and
 * apply in the miscellaneous settings dialog.
 */
public class AbstractSaveSettingsState extends ApplicationModelState {

	public AbstractSaveSettingsState(final ApplicationModelState lastState, final boolean addToHistory) {
		super(lastState, addToHistory);
	}

	@Override
	public void onEnter() {
		ApplicationSettings.getInstance().writeSettingsToFile();
	}

}
