package sudoku.state;

import javafx.scene.input.KeyCode;
import sudoku.core.ViewController;
import sudoku.model.SudokuPuzzle;
import sudoku.state.cell.active.ActiveCellState;
import sudoku.state.cell.active.AutomaticallyInactiveCellState;
import sudoku.state.cell.active.DefaultCellActiveState;
import sudoku.state.cell.active.ManuallyInactiveCellState;
import sudoku.view.puzzle.SudokuPuzzleCell;

/**
 * This class represents the state of the application when the selection changes
 * by arrow key input. Each constructor more or less corresponds to a subclass
 * of DefaultCellActiveState.
 */
public class SelectionChangedState extends ApplicationModelState {

	private final KeyCode keyCode;

	private final int col;

	private final int row;

	public SelectionChangedState(DefaultCellActiveState cellActiveState, ApplicationModelState lastState) {
		this(null, cellActiveState, lastState);
	}

	public SelectionChangedState(KeyCode keyCode, DefaultCellActiveState cellActiveState,
			ApplicationModelState lastState) {
		super(lastState);
		this.row = cellActiveState.getCell().getRow();
		this.col = cellActiveState.getCell().getCol();
		this.keyCode = keyCode;
		this.cellActiveStates[this.col][this.row] = cellActiveState;
	}

	@Override
	public void onEnter() {
		final DefaultCellActiveState cellState = this.cellActiveStates[this.col][this.row];
		if (cellState instanceof ActiveCellState) {
			this.unselectAllOtherCells();
			this.selectedCellRow = this.row;
			this.selectedCellCol = this.col;
		} else if (cellState.getLastState() instanceof ManuallyInactiveCellState) {
			this.resetSelectedCellIndices();
		} else if (cellState.getLastState() instanceof AutomaticallyInactiveCellState) {
			this.adjustSelectionModel();
		}
	}

	private void adjustSelectionModel() {
		if (KeyCode.UP == this.keyCode) {
			this.selectedCellRow--;
		} else if (KeyCode.DOWN == this.keyCode) {
			this.selectedCellRow++;
		} else if (KeyCode.LEFT == this.keyCode) {
			this.selectedCellCol--;
		} else if (KeyCode.RIGHT == this.keyCode) {
			this.selectedCellCol++;
		}
		final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(this.selectedCellCol,
				this.selectedCellRow);
		sudokuPuzzleCell.setActiveState(new ActiveCellState(sudokuPuzzleCell.getActiveState()));
	}

	private void resetSelectedCellIndices() {
		this.selectedCellRow = -1;
		this.selectedCellCol = -1;
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
		final DefaultCellActiveState otherCellState = this.cellActiveStates[colIndex][rowIndex];
		final SudokuPuzzleCell otherCell = otherCellState.getCell();
		if (otherCellState instanceof ActiveCellState) {
			otherCell.unselect(true);
		}
	}
}