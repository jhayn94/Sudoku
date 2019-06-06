package sudoku.state.model.settings;

import org.apache.logging.log4j.util.Strings;

import sudoku.core.HodokuFacade;
import sudoku.core.ViewController;
import sudoku.model.ApplicationSettings;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.settings.MiscellaneousSettingsView;
import sudoku.view.sidebar.PuzzleStatsPane;

/**
 * This class updates the state of the application when the user clicks save and
 * apply in the miscellaneous settings dialog.
 */
public class SaveMiscellaneousSettingsState extends AbstractSaveSettingsState {

	public SaveMiscellaneousSettingsState(final ApplicationModelState lastState) {
		super(lastState, false);
	}

	@Override
	public void onEnter() {
		final boolean isShowPuzzleProgress = this.updateSettings();
		this.updateRemainingScoreTextField(isShowPuzzleProgress);
		super.onEnter();
	}

	private boolean updateSettings() {
		final MiscellaneousSettingsView miscellaneousSettingsView = ViewController.getInstance()
				.getMiscellaneousSettingsView();
		final boolean isAutoManageCandidates = miscellaneousSettingsView.getAutoManageCandidatesCheckBox().isSelected();
		ApplicationSettings.getInstance().setAutoManageCandidates(isAutoManageCandidates);
		final boolean isShowPuzzleProgress = miscellaneousSettingsView.getShowPuzzleProgressCheckBox().isSelected();
		ApplicationSettings.getInstance().setShowPuzzleProgress(isShowPuzzleProgress);
		final boolean useDigitButtonsForMouseCheckBox = miscellaneousSettingsView.getUseDigitButtonsForMouseCheckBox()
				.isSelected();
		ApplicationSettings.getInstance().setUseDigitButtonsForMouseActions(useDigitButtonsForMouseCheckBox);
		return isShowPuzzleProgress;
	}

	private void updateRemainingScoreTextField(final boolean isShowPuzzleProgress) {
		final PuzzleStatsPane puzzleStatsPane = ViewController.getInstance().getPuzzleStatsPane();
		if (isShowPuzzleProgress) {
			final int remainingScore = HodokuFacade.getInstance().getScoreForPuzzle(this.sudokuPuzzleValues, false);
			if (remainingScore != 0) {
				puzzleStatsPane.getRemainingRatingTextField().setText(String.valueOf(remainingScore));
			}
		} else {
			puzzleStatsPane.getRemainingRatingTextField().setText(Strings.EMPTY);
		}
	}
}
