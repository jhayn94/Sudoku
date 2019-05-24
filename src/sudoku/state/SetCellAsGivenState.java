package sudoku.state;

/**
 * This class contains methods to set a cell as a given cell. TODO - does this
 * need to be a state? Should it be part of a "new puzzle" state?
 */
public class SetCellAsGivenState extends ApplicationModelState {

	public SetCellAsGivenState(final ApplicationModelState lastState) {
		super(lastState);
	}

	@Override
	public void onEnter() {
		this.updateFixedCellTypeCssClass(GIVEN_CELL_CSS_CLASS);
		this.getSelectedCell().setCellGiven(true);
	}

}
