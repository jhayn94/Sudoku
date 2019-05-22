package sudoku.state;

import sudoku.model.SudokuPuzzle;
import sudoku.state.cell.DefaultSudokuCellState;
import sudoku.state.cell.SelectedCellState;
import sudoku.view.puzzle.SudokuPuzzleCell;

/**
 * This class represents the state of the application when a cell changes.
 */
public class CellChangedState extends ApplicationModelState {

	private final int col;

	private final int row;

	public CellChangedState(int row, int col, DefaultSudokuCellState cellState, ApplicationModelState lastState) {
		super(lastState);
		this.row = row;
		this.col = col;
		this.cellStates[col][row] = cellState;
		this.onEnter();
	}

	@Override
	protected void onEnter() {
		final DefaultSudokuCellState cellState = this.cellStates[this.col][this.row];
		if (cellState instanceof SelectedCellState) {
			this.unselectAllOtherCells();
			this.selectedCellRow = this.row;
			this.selectedCellCol = this.col;
		} else if (cellState.getLastState() instanceof SelectedCellState) {
			this.selectedCellRow = -1;
			this.selectedCellCol = -1;
		}
	}

	private void unselectAllOtherCells() {
		for (int rowIndex = 0; rowIndex < SudokuPuzzle.NUMBER_OF_CELLS_PER_DIMENSION; rowIndex++) {
			for (int colIndex = 0; colIndex < SudokuPuzzle.NUMBER_OF_CELLS_PER_DIMENSION; colIndex++) {
				if (rowIndex != this.row || colIndex != this.col) {
					this.resetSelectedStateForCell(rowIndex, colIndex);
				}
			}
		}
	}

	/**
	 * Checks if the cell is in a selected state, and if it is, resets it to a
	 * non-selected state.
	 */
	private void resetSelectedStateForCell(int rowIndex, int colIndex) {
		final DefaultSudokuCellState otherCellState = this.cellStates[colIndex][rowIndex];
		final SudokuPuzzleCell otherCell = otherCellState.getCell();
		if (otherCellState instanceof SelectedCellState) {
			otherCell.unselect();
		}
	}
}