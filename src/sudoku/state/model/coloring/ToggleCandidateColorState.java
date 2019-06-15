package sudoku.state.model.coloring;

import java.util.Set;

import javafx.scene.input.KeyCode;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.puzzle.SudokuPuzzleCell;
import sudoku.view.util.ColorUtils.ColorState;

/**
 * This class updates the state of the application to reply to set the active
 * coloring candidate label's color for the specified cell.
 */
public class ToggleCandidateColorState extends ApplicationModelState {

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

		if (this.sudokuPuzzleValues.getFixedCellDigit(selectedCell.getRow(), selectedCell.getCol()) == 0) {
			final Set<Integer> candidatesForCell = this.sudokuPuzzleValues.getCandidateDigitsForCell(
					this.sudokuPuzzleStyle.getSelectedCellRow(), this.sudokuPuzzleStyle.getSelectedCellCol());
			final boolean isCandidateVisible = candidatesForCell.contains(this.sudokuPuzzleStyle.getActiveCandidateDigit());
			if (isCandidateVisible) {

				final ColorState colorStateToApply = ColorState.getFromKeyCode(this.lastKeyCode, this.isShiftDown);
				this.setCandidateColorForCell(selectedCell.getRow(), selectedCell.getCol(), colorStateToApply,
						this.sudokuPuzzleStyle.getActiveCandidateDigit());
			}
		}
	}

}
