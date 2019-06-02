package sudoku.state.model.settings;

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
		Difficulty.getValidDifficulties().forEach(difficulty -> {
			final TextField maxScoreInput = difficultySettingsView.getMaxScoreInput(difficulty);
			String maxScoreText = maxScoreInput.getText();
			if (maxScoreText.isEmpty()) {
				maxScoreText = String.valueOf(0);
			}
			final int maxScore = Integer.parseInt(maxScoreText);
			ApplicationSettings.getInstance().setMaxScoreForDifficulty(difficulty.name(), maxScore);
		});
		super.onEnter();
	}
}
