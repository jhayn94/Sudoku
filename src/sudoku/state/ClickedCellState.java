package sudoku.state;

import sudoku.core.ViewController;
import sudoku.view.puzzle.SudokuPuzzleCell;
import sudoku.view.util.MouseMode;

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
		if (MouseMode.SELECT_CELLS == this.mouseMode) {
			if (this.sudokuPuzzleStyle.getSelectedCellRow() != -1 && this.sudokuPuzzleStyle.getSelectedCellCol() != -1) {
				this.getSelectedCell().setIsSelected(false);
			}
			this.updateSelectedCell();
		} else if (MouseMode.TOGGLE_CANDIDATES == this.mouseMode) {
			final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(this.row, this.col);
			this.toggleCandidateActiveForCell(this.sudokuPuzzleStyle.getActiveColorCandidateDigit(), sudokuPuzzleCell);
		} else if (MouseMode.COLOR_CELLS == this.mouseMode) {

		} else {
			// MouseMode.COLOR_CANDIDATES case.

		}
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