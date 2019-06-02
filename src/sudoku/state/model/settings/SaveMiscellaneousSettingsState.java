package sudoku.state.model.settings;

import org.apache.logging.log4j.util.Strings;

import sudoku.core.HodokuFacade;
import sudoku.core.ViewController;
import sudoku.model.ApplicationSettings;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.settings.MiscellaneousSettingsView;

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
		final MiscellaneousSettingsView miscellaneousSettingsView = ViewController.getInstance()
				.getMiscellaneousSettingsView();
		final boolean isAutoManageCandidates = miscellaneousSettingsView.getAutoManageCandidatesCheckBox().isSelected();
		ApplicationSettings.getInstance().setAutoManageCandidates(isAutoManageCandidates);
		final boolean isShowPuzzleProgress = miscellaneousSettingsView.getShowPuzzleProgressCheckBox().isSelected();
		ApplicationSettings.getInstance().setShowPuzzleProgress(isShowPuzzleProgress);
		if (isShowPuzzleProgress) {
			final int remainingScore = HodokuFacade.getInstance().getScoreForPuzzle(this.sudokuPuzzleValues, false);
			ViewController.getInstance().getPuzzleStatsPane().getRemainingRatingTextField()
					.setText(String.valueOf(remainingScore));
		} else {
			ViewController.getInstance().getPuzzleStatsPane().getRemainingRatingTextField().setText(Strings.EMPTY);
		}
		super.onEnter();
	}
}
