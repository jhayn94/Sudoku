package sudoku.state.model.settings;

import sudoku.state.model.ApplicationModelState;

/**
 * This class updates the state of the application when the user clicks save and
 * apply in the miscellaneous settings dialog.
 */
public class SavePuzzleGenerationSettingsState extends AbstractSaveSettingsState {

	public SavePuzzleGenerationSettingsState(final ApplicationModelState lastState) {
		super(lastState, false);
	}

	@Override
	public void onEnter() {

		super.onEnter();
	}
}
