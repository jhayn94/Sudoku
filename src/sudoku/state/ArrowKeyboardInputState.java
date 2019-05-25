package sudoku.state;

import javafx.scene.input.KeyCode;
import sudoku.model.SudokuPuzzleValues;

/**
 * This class updates the state of the application when the selection changes by
 * arrow key input. This should have the effect of moving the selected cell, if
 * there is one.
 */
public class ArrowKeyboardInputState extends ApplicationModelState {

	public ArrowKeyboardInputState(final KeyCode keyCode, final ApplicationModelState lastState) {
		super(lastState);
		this.lastKeyCode = keyCode;
	}

	@Override
	public void onEnter() {
		this.adjustSelectionModel();
	}

	private void adjustSelectionModel() {
		int selectedCellRow = this.sudokuPuzzleStyle.getSelectedCellRow();
		int selectedCellCol = this.sudokuPuzzleStyle.getSelectedCellCol();
		// No selection -> do nothing.
		if (selectedCellRow == -1 || selectedCellCol == -1) {
			return;
		}

		this.getSelectedCell().setIsSelected(false);
		if (KeyCode.UP == this.lastKeyCode && selectedCellRow > 0) {
			selectedCellRow--;
		} else if (KeyCode.DOWN == this.lastKeyCode && selectedCellRow < SudokuPuzzleValues.CELLS_PER_HOUSE - 1) {
			selectedCellRow++;
		} else if (KeyCode.LEFT == this.lastKeyCode && selectedCellCol > 0) {
			selectedCellCol--;
		} else if (KeyCode.RIGHT == this.lastKeyCode && selectedCellCol < SudokuPuzzleValues.CELLS_PER_HOUSE - 1) {
			selectedCellCol++;
		}
		this.sudokuPuzzleStyle.setSelectedCellRow(selectedCellRow);
		this.sudokuPuzzleStyle.setSelectedCellCol(selectedCellCol);
		this.getSelectedCell().setIsSelected(true);
	}

}