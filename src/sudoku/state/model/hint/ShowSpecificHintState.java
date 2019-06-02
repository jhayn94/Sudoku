package sudoku.state.model.hint;

import sudoku.SolutionType;
import sudoku.core.HodokuFacade;
import sudoku.core.ViewController;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.hint.HintButtonPane;
import sudoku.view.hint.HintTextArea;
import sudoku.view.util.LabelConstants;

/**
 * This class updates the state of the application when the user requests a
 * specific hint (i.e. the exact next step).
 */
public class ShowSpecificHintState extends ApplicationModelState {

	public ShowSpecificHintState(final ApplicationModelState lastState) {
		super(lastState, false);
	}

	@Override
	public void onEnter() {
		this.displayedHint = HodokuFacade.getInstance().getHint(this.sudokuPuzzleValues);
		final HintTextArea hintTextArea = ViewController.getInstance().getHintTextArea();
		final String newHintText = SolutionType.GIVE_UP == this.displayedHint.getType() ? LabelConstants.NO_MOVES
				: this.displayedHint.toString();
		hintTextArea.getHintTextArea().setText(newHintText);
		final HintButtonPane hintButtonPane = ViewController.getInstance().getHintButtonPane();
		hintButtonPane.getApplyHintButton().setDisable(false);
		hintButtonPane.getHideHintButton().setDisable(false);
	}
}