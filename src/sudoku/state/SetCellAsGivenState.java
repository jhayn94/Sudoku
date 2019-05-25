package sudoku.state;

/**
 * This class updates the state of the application when cells are set as given.
 * TODO - does this need to be a state? Should it be part of a "new puzzle"
 * state?
 */
public class SetCellAsGivenState extends ApplicationModelState {

	public SetCellAsGivenState(final ApplicationModelState lastState) {
		super(lastState);
	}

	@Override
	public void onEnter() {
		this.updateFixedCellTypeCssClass(GIVEN_CELL_CSS_CLASS);
		this.getSelectedCell().setCellGiven(true);
		this.reapplyActiveFilter();
	}

}
