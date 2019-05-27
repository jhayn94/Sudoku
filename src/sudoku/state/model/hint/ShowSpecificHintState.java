package sudoku.state.model.hint;

import sudoku.core.HodokuFacade;
import sudoku.core.ViewController;
import sudoku.state.ApplicationModelState;
import sudoku.view.hint.HintButtonPane;
import sudoku.view.hint.HintTextArea;
import sudoku.view.util.LabelConstants;

/**
 * This class updates the state of the application when a cell's digit is set.
 */
public class ShowSpecificHintState extends ApplicationModelState {

	public ShowSpecificHintState(final ApplicationModelState lastState) {
		super(lastState, true);
	}

	@Override
	public void onEnter() {
		this.displayedHint = HodokuFacade.getInstance().getHint(this.sudokuPuzzleValues.getStringRepresentation(false));
		final HintTextArea hintTextArea = ViewController.getInstance().getHintTextArea();
		final String newHintText = this.displayedHint == null ? LabelConstants.NO_MOVES : this.displayedHint.toString();
		hintTextArea.getHintTextArea().setText(newHintText);
		final HintButtonPane hintButtonPane = ViewController.getInstance().getHintButtonPane();
		hintButtonPane.getApplyHintButton().setDisable(false);
		hintButtonPane.getHideHintButton().setDisable(false);
		hintButtonPane.getSpecificHintButton().setDisable(true);
		hintButtonPane.getVagueHintButton().setDisable(true);
	}
}