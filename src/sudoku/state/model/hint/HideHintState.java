package sudoku.state.model.hint;

import org.apache.logging.log4j.util.Strings;

import sudoku.core.ViewController;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.hint.HintButtonPane;
import sudoku.view.hint.HintTextArea;
import sudoku.view.util.ColorUtils;

/**
 * This class updates the state of the application to hide a displayed hint
 * (i.e. clear the view).
 */
public class HideHintState extends ApplicationModelState {

	public HideHintState(final ApplicationModelState lastState) {
		super(lastState, false);
	}

	@Override
	public void onEnter() {
		this.displayedHint = null;
		final HintTextArea hintTextArea = ViewController.getInstance().getHintTextArea();
		hintTextArea.getHintTextArea().setText(Strings.EMPTY);
		final HintButtonPane hintButtonPane = ViewController.getInstance().getHintButtonPane();
		hintButtonPane.getApplyHintButton().setDisable(true);
		hintButtonPane.getHideHintButton().setDisable(true);
		hintButtonPane.getSpecificHintButton().setDisable(false);
		hintButtonPane.getVagueHintButton().setDisable(false);
		this.resetColorStates(false, true, ColorUtils.getHintColorStates());
	}
}