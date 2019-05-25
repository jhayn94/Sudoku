package sudoku.state;

import javafx.scene.input.KeyCode;
import sudoku.model.SudokuPuzzle;

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
		// No selection -> do nothing.
		if (this.selectedCellRow == -1 || this.selectedCellCol == -1) {
			return;
		}

		this.getSelectedCell().setIsSelected(false);
		if (KeyCode.UP == this.lastKeyCode && this.selectedCellRow > 0) {
			this.selectedCellRow--;
		} else if (KeyCode.DOWN == this.lastKeyCode && this.selectedCellRow < SudokuPuzzle.CELLS_PER_HOUSE - 1) {
			this.selectedCellRow++;
		} else if (KeyCode.LEFT == this.lastKeyCode && this.selectedCellCol > 0) {
			this.selectedCellCol--;
		} else if (KeyCode.RIGHT == this.lastKeyCode && this.selectedCellCol < SudokuPuzzle.CELLS_PER_HOUSE - 1) {
			this.selectedCellCol++;
		}
		this.getSelectedCell().setIsSelected(true);
	}

}