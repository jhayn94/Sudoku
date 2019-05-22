package sudoku.state;

import sudoku.state.cell.action.DefaultCellActionState;

/**
 * This class represents the state of the application when a cell changes.
 */
public class CellChangedState extends ApplicationModelState {

	private final int col;

	private final int row;

	public CellChangedState(DefaultCellActionState cellActionState, ApplicationModelState lastState) {
		super(lastState);
		this.row = cellActionState.getCell().getRow();
		this.col = cellActionState.getCell().getCol();
		this.cellActionStates[this.col][this.row] = cellActionState;
		this.onEnter();
	}

	@Override
	public void onEnter() {

	}

}