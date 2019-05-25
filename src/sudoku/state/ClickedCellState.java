package sudoku.state;

/**
 * This class updates the state of the application when the user clicks a cell.
 * In general, this will only affect the selected cell.
 */
public class ClickedCellState extends ApplicationModelState {

	private final int col;

	private final int row;

	public ClickedCellState(final int row, final int col, final ApplicationModelState lastState) {
		super(lastState);
		this.row = row;
		this.col = col;
	}

	@Override
	public void onEnter() {
		this.unselectCurrentlySelectedCell();
		// Cell was already selected.
		if (this.row == this.selectedCellRow && this.col == this.selectedCellCol) {
			this.resetSelectedCellIndices();
		} else {
			this.selectedCellRow = this.row;
			this.selectedCellCol = this.col;
			this.getSelectedCell().setIsSelected(true);
		}
	}

	private void resetSelectedCellIndices() {
		this.selectedCellRow = -1;
		this.selectedCellCol = -1;
	}

	private void unselectCurrentlySelectedCell() {
		if (this.selectedCellRow != -1 && this.selectedCellCol != -1) {
			this.getSelectedCell().setIsSelected(false);
		}
	}

}