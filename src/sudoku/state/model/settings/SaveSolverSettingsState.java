package sudoku.state.model.settings;

import java.util.List;

import sudoku.StepConfig;
import sudoku.model.ApplicationSettings;
import sudoku.state.model.ApplicationModelState;

/**
 * This class updates the state of the application when the user clicks save and
 * apply in the miscellaneous settings dialog.
 */
public class SaveSolverSettingsState extends AbstractSaveSettingsState {

	private final List<StepConfig> stepConfigs;

	public SaveSolverSettingsState(final List<StepConfig> stepConfigs, final ApplicationModelState lastState) {
		super(lastState, false);
		this.stepConfigs = stepConfigs;
	}

	@Override
	public void onEnter() {
		ApplicationSettings.getInstance().setSolverConfig(this.stepConfigs);
		super.onEnter();
	}
}
