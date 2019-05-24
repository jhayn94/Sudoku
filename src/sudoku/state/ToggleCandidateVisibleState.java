package sudoku.state;

import java.util.List;

import javafx.scene.input.KeyCode;
import sudoku.view.puzzle.SudokuPuzzleCell;

/**
 * This class contains methods to reply to a CTRL + digit key press, which
 * should toggle a candidate's visibility in the active cell.
 */
public class ToggleCandidateVisibleState extends ApplicationModelState {

	public ToggleCandidateVisibleState(final KeyCode keyCode, final ApplicationModelState lastState) {
		super(lastState);
		this.lastKeyCode = keyCode;
	}

	@Override
	public void onEnter() {
		final int pressedDigit = Integer.parseInt(this.lastKeyCode.getName());
		final SudokuPuzzleCell selectedCell = this.getSelectedCell();

		if (!selectedCell.isCellFixed()) {
			final List<Integer> candidatesForCell = this.puzzleModel.getCandidateDigitsForCell(this.selectedCellRow,
					this.selectedCellCol);
			final boolean isCandidateVisible = candidatesForCell.contains(pressedDigit);
			selectedCell.setCandidateVisible(pressedDigit, !isCandidateVisible);
			if (isCandidateVisible) {
				candidatesForCell.remove((Object) pressedDigit);
			} else {
				candidatesForCell.add(pressedDigit);
			}
		}
	}

}
