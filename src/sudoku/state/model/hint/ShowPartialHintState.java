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
public class ShowPartialHintState extends ApplicationModelState {

	public ShowPartialHintState(final ApplicationModelState lastState) {
		super(lastState, false);
	}

	@Override
	public void onEnter() {
		this.resetColorStates(false, true, ColorUtils.getHintColorStates());
		this.displayedHint = HodokuFacade.getInstance().getHint(this.sudokuPuzzleValues);
		final TextArea hintTextArea = ViewController.getInstance().getHintTextArea().getHintTextArea();
		final String newHintText = SolutionType.GIVE_UP == this.displayedHint.getType() ? LabelConstants.NO_MOVES
				: LabelConstants.VAGUE_HINT_PREFIX + this.displayedHint.toString(1);
		hintTextArea.setText(newHintText);
		final HintButtonPane hintButtonPane = ViewController.getInstance().getHintButtonPane();
		hintButtonPane.getApplyHintButton().setDisable(false);
		hintButtonPane.getHideHintButton().setDisable(false);
	}
}