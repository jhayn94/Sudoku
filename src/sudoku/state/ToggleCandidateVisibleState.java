package sudoku.state;

import java.util.List;

import javafx.scene.input.KeyCode;
import sudoku.view.puzzle.SudokuPuzzleCell;

/**
 * This class updates the state of the application to reply to a CTRL + digit
 * key press, which should toggle a candidate's visibility in the active cell.
 */
public class ToggleCandidateVisibleState extends ApplicationModelState {

	public ToggleCandidateVisibleState(final KeyCode keyCode, final ApplicationModelState lastState) {
		super(lastState, true);
		this.lastKeyCode = keyCode;
	}

	@Override
	public void onEnter() {
		final int pressedDigit = Integer.parseInt(this.lastKeyCode.getName());
		final SudokuPuzzleCell selectedCell = this.getSelectedCell();

		if (!selectedCell.isCellFixed()) {
			final List<Integer> candidatesForCell = this.sudokuPuzzleValues.getCandidateDigitsForCell(
					this.sudokuPuzzleStyle.getSelectedCellRow(), this.sudokuPuzzleStyle.getSelectedCellCol());
			final boolean isCandidateVisible = candidatesForCell.contains(pressedDigit);
			selectedCell.setCandidateVisible(pressedDigit, !isCandidateVisible);
			if (isCandidateVisible) {
				candidatesForCell.remove((Object) pressedDigit);
			} else {
				candidatesForCell.add(pressedDigit);
			}
		}
		this.reapplyActiveFilter();
	}

}
