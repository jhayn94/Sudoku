package sudoku.state.model.hint;

import javafx.scene.control.TextArea;
import sudoku.SolutionType;
import sudoku.core.HodokuFacade;
import sudoku.core.ViewController;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.hint.HintButtonPane;
import sudoku.view.util.ColorUtils;
import sudoku.view.util.LabelConstants;

/**
 * This class updates the state of the application when the user requests a
 * vague hint (i.e. the next step's type).
 */
public class ShowVagueHintState extends ApplicationModelState {

	public ShowVagueHintState(final ApplicationModelState lastState) {
		super(lastState, false);
	}

	@Override
	public void onEnter() {
		ViewController.getInstance().getRootPane().removeAllAnnotations();
		this.resetColorStates(false, true, ColorUtils.getHintColorStates());
		this.displayedHint = HodokuFacade.getInstance().getHint(this.sudokuPuzzleValues);
		final TextArea hintTextArea = ViewController.getInstance().getHintTextArea().getHintTextArea();
		if (this.displayedHint == null) {
			hintTextArea.setText(LabelConstants.PUZZLE_SOLVED);
		} else {
			final String newHintText = SolutionType.GIVE_UP == this.displayedHint.getType() ? LabelConstants.NO_MOVES
					: LabelConstants.VAGUE_HINT_PREFIX + this.displayedHint.getType().getStepName();
			hintTextArea.setText(newHintText);
			final HintButtonPane hintButtonPane = ViewController.getInstance().getHintButtonPane();
			if (SolutionType.GIVE_UP != this.displayedHint.getType()) {
				hintButtonPane.getApplyHintButton().setDisable(false);
				hintButtonPane.getHideHintButton().setDisable(false);
			}
		}
	}
}