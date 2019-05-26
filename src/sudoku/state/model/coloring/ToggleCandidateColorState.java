package sudoku.state.model.coloring;

import java.util.List;

import javafx.scene.input.KeyCode;
import sudoku.state.ApplicationModelState;
import sudoku.view.puzzle.SudokuPuzzleCell;
import sudoku.view.util.ColorUtils.ColorState;

/**
 * This class updates the state of the application to reply to set the active
 * coloring candidate label's color for the specified cell.
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

				final ColorState colorStateToApply = ColorState.getFromKeyCode(this.lastKeyCode, false);
				this.updateCandidateColorForCell(selectedCell, colorStateToApply);
			}
		}
	}

}
