package sudoku.state.model.settings;

import java.util.Arrays;

import javafx.scene.control.TextField;
import sudoku.core.ViewController;
import sudoku.model.ApplicationSettings;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.settings.DifficultySettingsView;
import sudoku.view.util.Difficulty;

/**
 * This class updates the state of the application when the user clicks save and
 * apply in the miscellaneous settings dialog.
 */
public class SaveDifficultySettingsState extends AbstractSaveSettingsState {

	public SaveDifficultySettingsState(final ApplicationModelState lastState) {
		super(lastState, false);
	}

	@Override
	public void onEnter() {
		final DifficultySettingsView difficultySettingsView = ViewController.getInstance().getDifficultySettingsView();
		Arrays.asList(Difficulty.values()).forEach(difficulty -> {
			final TextField maxScoreInput = difficultySettingsView.getMaxScoreInput(difficulty);
			final int maxScore = Integer.parseInt(maxScoreInput.getText());
			ApplicationSettings.getInstance().setMaxScoreForDifficulty(difficulty.name(), maxScore);
		});
		// TODO - map these to the Options settings from hodoku?
		super.onEnter();
	}
}
