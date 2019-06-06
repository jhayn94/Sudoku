package sudoku.state.model.hint;

import org.apache.logging.log4j.util.Strings;

import sudoku.core.ViewController;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.hint.HintButtonPane;
import sudoku.view.hint.HintTextArea;
import sudoku.view.util.ColorUtils;

/**
 * This class updates the state of the application to apply a displayed hint to
 * the puzzle.
 */
public class ApplyHintState extends ApplicationModelState {

	public ApplyHintState(final ApplicationModelState lastState) {
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
		this.resetColorStates(false, true, ColorUtils.getHintColorStates());
		// TODO - apply the hint to the SudokuPuzzleValues model + view.
	}
}