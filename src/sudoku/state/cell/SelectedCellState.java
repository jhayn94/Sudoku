package sudoku.state.cell;

/**
 * This class corresponds to a sudoku cell which is currently active, and can
 * receive input.
 */
public class SelectedCellState extends DefaultSudokuCellState {

	public SelectedCellState(DefaultSudokuCellState lastState) {
		super(lastState);
	}

	@Override
	protected void onEnter() {
	}

}
