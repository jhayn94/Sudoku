package sudoku.state.model.settings;

import sudoku.core.ViewController;
import sudoku.model.ApplicationSettings;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.settings.PuzzleGenerationSettingsView;
import sudoku.view.util.Difficulty;

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
		final PuzzleGenerationSettingsView puzzleGenerationSettingsView = ViewController.getInstance()
				.getPuzzleGenerationSettingsView();
		final String difficulty = puzzleGenerationSettingsView.getDifficultyComboBox().getComboBox().getSelectionModel()
				.getSelectedItem();
		final String mustContainTechnique = puzzleGenerationSettingsView.getMustContainTechniqueComboBox().getComboBox()
				.getSelectionModel().getSelectedItem();
		final boolean solveUpToStep = puzzleGenerationSettingsView.getSolveUpToStepCheckBox().isSelected();

		ApplicationSettings.getInstance().setDifficulty(Difficulty.valueOf(difficulty.toUpperCase().replace(" ", "_")));
		ApplicationSettings.getInstance().setMustContainStepWithName(mustContainTechnique);
		ApplicationSettings.getInstance().setSolveToRequiredStep(solveUpToStep);
		super.onEnter();
	}
}
