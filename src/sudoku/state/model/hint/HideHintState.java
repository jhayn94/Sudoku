package sudoku.state.model.hint;

import org.apache.logging.log4j.util.Strings;

import sudoku.core.ViewController;
import sudoku.state.ApplicationModelState;
import sudoku.view.hint.HintButtonPane;
import sudoku.view.hint.HintTextArea;

/**
 * This class updates the state of the application when a cell's digit is set.
 */
public class HideHintState extends ApplicationModelState {

	public HideHintState(final ApplicationModelState lastState) {
		super(lastState, true);
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
	}
}