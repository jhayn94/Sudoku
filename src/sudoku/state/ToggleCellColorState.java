package sudoku.state;

import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import sudoku.view.puzzle.SudokuPuzzleCell;
import sudoku.view.util.ColorUtils.ColorState;

/**
 * This class updates the state of the application to reply to a ASDFG key
 * press, which should set the active cell's color. Note that SHIFT also may be
 * pressed as well to apply a slightly different color.
 */
public class ToggleCellColorState extends ApplicationModelState {

	private final boolean isShiftDown;

	public ToggleCellColorState(final KeyCode keyCode, final boolean isShiftDown, final ApplicationModelState lastState) {
		super(lastState);
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

		final ColorState currentColorState = this.cellColorStates[col][row];
		final ColorState colorStateToApply = ColorState.getFromKeyCode(this.lastKeyCode, this.isShiftDown);

		final ObservableList<String> styleClass = selectedCell.getStyleClass();
		if (colorStateToApply == currentColorState) {
			styleClass.remove(currentColorState.getCssClass());
			this.cellColorStates[col][row] = ColorState.NONE;
		} else if (currentColorState != ColorState.NONE) {
			styleClass.remove(currentColorState.getCssClass());
			styleClass.add(colorStateToApply.getCssClass());
			this.cellColorStates[col][row] = colorStateToApply;
		} else {
			styleClass.add(colorStateToApply.getCssClass());
			this.cellColorStates[col][row] = colorStateToApply;
		}
	}

}
