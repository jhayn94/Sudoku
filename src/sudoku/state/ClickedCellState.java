package sudoku.state;

/**
 * This class updates the state of the application when the user clicks a cell.
 * In general, this will only affect the selected cell.
 */
public class ClickedCellState extends ApplicationModelState {

	private final int col;

	private final int row;

	public ClickedCellState(final int row, final int col, final ApplicationModelState lastState) {
		super(lastState, false);
		this.row = row;
		this.col = col;
	}

	@Override
	public void onEnter() {
		if (this.sudokuPuzzleStyle.getSelectedCellRow() != -1 && this.sudokuPuzzleStyle.getSelectedCellCol() != -1) {
			this.getSelectedCell().setIsSelected(false);
		}
		this.updateSelectedCell();
	}

	private void updateSelectedCell() {
		// Cell was already selected.
		if (this.row == this.sudokuPuzzleStyle.getSelectedCellRow()
				&& this.col == this.sudokuPuzzleStyle.getSelectedCellCol()) {
			this.sudokuPuzzleStyle.resetSelectedCellIndices();
		} else {
			this.sudokuPuzzleStyle.setSelectedCellRow(this.row);
			this.sudokuPuzzleStyle.setSelectedCellCol(this.col);
			this.getSelectedCell().setIsSelected(true);
		}
	}

}