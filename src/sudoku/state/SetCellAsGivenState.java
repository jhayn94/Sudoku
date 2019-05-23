package sudoku.state;

/**
 * This class corresponds to a sudoku cell which is a given and cannot be
 * changed.
 */
public class SetCellAsGivenState extends ApplicationModelState {

	public SetCellAsGivenState(final ApplicationModelState lastState) {
		super(lastState);
	}

	@Override
	public void onEnter() {
		this.updateCssClass(GIVEN_CELL_CSS_CLASS);
		this.getSelectedCell().setCellGiven(true);
	}

}
