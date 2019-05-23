package sudoku.state;

import java.util.List;

import javafx.scene.input.KeyCode;
import sudoku.view.puzzle.SudokuPuzzleCell;

/**
 * This class corresponds to a sudoku cell which is set by the user. Candidates
 * cannot be toggled, but the fixed digit may be changed or deleted.
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
