package sudoku.state.model.coloring;

import javafx.scene.input.KeyCode;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.puzzle.SudokuPuzzleCell;
import sudoku.view.util.ColorUtils.ColorState;

/**
 * This class updates the state of the application to reply to set the active
 * cell's color. Note that SHIFT also may be pressed as well to apply a slightly
 * different color.
 */
public class ToggleCellColorState extends ApplicationModelState {

	private final boolean isShiftDown;

	public ToggleCellColorState(final KeyCode keyCode, final boolean isShiftDown, final ApplicationModelState lastState) {
		super(lastState, false);
		this.lastKeyCode = keyCode;
		this.isShiftDown = isShiftDown;
	}

	@Override
	public void onEnter() {
		this.updateSelectedCellColor();
	}

	private void updateSelectedCellColor() {
		final SudokuPuzzleCell selectedCell = this.getSelectedCell();
		final int row = selectedCell.getRow();
		final int col = selectedCell.getCol();
		final ColorState colorStateToApply = ColorState.getFromKeyCode(this.lastKeyCode, this.isShiftDown);
		this.setColorStateForCell(row, col, colorStateToApply);
	}

}
