package sudoku.state;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import sudoku.core.ViewController;
import sudoku.view.puzzle.SudokuPuzzleCell;
import sudoku.view.util.ColorUtils.ColorState;

/**
 * This class updates the state of the application to reply to a CTRL + ASDFG
 * key press, which should set the active coloring candidate label's color for
 * the active cell.
 */
public class ToggleCandidateColorState extends ApplicationModelState {

	@SuppressWarnings("unused")
	private final boolean isShiftDown;

	public ToggleCandidateColorState(final KeyCode keyCode, final boolean isShiftDown,
			final ApplicationModelState lastState) {
		super(lastState, false);
		this.lastKeyCode = keyCode;
		this.isShiftDown = isShiftDown;
	}

	@Override
	public void onEnter() {
		final SudokuPuzzleCell selectedCell = this.getSelectedCell();

		if (!selectedCell.isCellFixed()) {
			final List<Integer> candidatesForCell = this.sudokuPuzzleValues.getCandidateDigitsForCell(
					this.sudokuPuzzleStyle.getSelectedCellRow(), this.sudokuPuzzleStyle.getSelectedCellCol());
			final boolean isCandidateVisible = candidatesForCell
					.contains(this.sudokuPuzzleStyle.getActiveColorCandidateDigit());
			if (isCandidateVisible) {
				this.updateCandidateColorForSelectedCell();
			}
		}
	}

	private void updateCandidateColorForSelectedCell() {
		final SudokuPuzzleCell selectedCell = this.getSelectedCell();
		final int row = selectedCell.getRow();
		final int col = selectedCell.getCol();
		final int activeColorCandidateDigit = this.sudokuPuzzleStyle.getActiveColorCandidateDigit();
		final ColorState currentColorState = this.sudokuPuzzleStyle.getCandidateColorState(row, col,
				activeColorCandidateDigit);
		final Label candidateLabelForDigit = ViewController.getInstance().getSudokuPuzzleCell(row, col)
				.getCandidateLabelForDigit(activeColorCandidateDigit);
		final ColorState colorStateToApply = ColorState.getFromKeyCode(this.lastKeyCode, false);

		final ObservableList<String> styleClass = candidateLabelForDigit.getStyleClass();
		if (colorStateToApply == currentColorState) {
			styleClass.remove(currentColorState.getCssClass());
			this.sudokuPuzzleStyle.setCandidateColorState(row, col, activeColorCandidateDigit, ColorState.NONE);
		} else {
			if (currentColorState != ColorState.NONE) {
				styleClass.remove(currentColorState.getCssClass());
			}
			styleClass.add(colorStateToApply.getCssClass());
			this.sudokuPuzzleStyle.setCandidateColorState(row, col, activeColorCandidateDigit, colorStateToApply);
		}
	}

}
