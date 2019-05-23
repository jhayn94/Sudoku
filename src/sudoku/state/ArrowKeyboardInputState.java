package sudoku.state;

import javafx.scene.input.KeyCode;
import sudoku.model.SudokuPuzzle;

/**
 * This class represents the state of the application when the selection changes
 * by arrow key input. Each constructor more or less corresponds to a subclass
 * of DefaultCellActiveState.
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
		this.getSelectedCell().getStyleClass().remove(SELECTED_CELL_CSS_CLASS);
		if (KeyCode.UP == this.lastKeyCode && this.selectedCellRow > 0) {
			this.selectedCellRow--;
		} else if (KeyCode.DOWN == this.lastKeyCode
				&& this.selectedCellRow < SudokuPuzzle.NUMBER_OF_CELLS_PER_DIMENSION - 1) {
			this.selectedCellRow++;
		} else if (KeyCode.LEFT == this.lastKeyCode && this.selectedCellCol > 0) {
			this.selectedCellCol--;
		} else if (KeyCode.RIGHT == this.lastKeyCode
				&& this.selectedCellCol < SudokuPuzzle.NUMBER_OF_CELLS_PER_DIMENSION - 1) {
			this.selectedCellCol++;
		}
		this.getSelectedCell().getStyleClass().add(SELECTED_CELL_CSS_CLASS);
	}

}