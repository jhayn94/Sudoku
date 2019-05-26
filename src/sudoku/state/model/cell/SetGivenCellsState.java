package sudoku.state.model.cell;

import sudoku.state.ApplicationModelState;

/**
 * This class updates the state of the application when cells are set as given.
 * TODO - does this need to be a state? Should it be part of a "new puzzle"
 * state?
 */
public class SetGivenCellsState extends ApplicationModelState {

	public SetGivenCellsState(final ApplicationModelState lastState) {
		super(lastState, false);
		// Don't want the user to be able to undo back to another puzzle.
		this.applicationStateHistory.clearRedoStack();
	}

	@Override
	public void onEnter() {
		// TODO - instead of this, take a sudoku as a string and update everything
		// accordingly.
		this.updateFixedCellTypeCssClass(this.getSelectedCell(), GIVEN_CELL_CSS_CLASS);
		this.getSelectedCell().setCellGiven(true);
		this.reapplyActiveFilter();
	}

}
