package sudoku.state;

/**
 * This class represents the state of the application when the selection changes
 * by arrow key input. Each constructor more or less corresponds to a subclass
 * of DefaultCellActiveState.
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
			this.getSelectedCell().getStyleClass().add(SELECTED_CELL_CSS_CLASS);
		}
	}

	private void resetSelectedCellIndices() {
		this.selectedCellRow = -1;
		this.selectedCellCol = -1;
	}

	private void unselectCurrentlySelectedCell() {
		if (this.selectedCellRow != -1 && this.selectedCellCol != -1) {
			this.getSelectedCell().getStyleClass().remove(SELECTED_CELL_CSS_CLASS);
		}
	}

}