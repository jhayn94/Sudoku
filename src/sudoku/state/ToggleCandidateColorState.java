package sudoku.state;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import sudoku.core.ViewController;
import sudoku.view.puzzle.SudokuPuzzleCell;
import sudoku.view.util.ColorUtils.ColorState;

/**
 * This class contains methods to reply to a SHIFT + digit key press, which
 * should set that candidate for the active cell
 */
public class ToggleCandidateColorState extends ApplicationModelState {

	private final boolean isShiftDown;

	public ToggleCandidateColorState(final KeyCode keyCode, final boolean isShiftDown,
			final ApplicationModelState lastState) {
		super(lastState);
		this.lastKeyCode = keyCode;
		this.isShiftDown = isShiftDown;
	}

	@Override
	public void onEnter() {
		final SudokuPuzzleCell selectedCell = this.getSelectedCell();

		if (!selectedCell.isCellFixed()) {
			final List<Integer> candidatesForCell = this.puzzleModel.getCandidateDigitsForCell(this.selectedCellRow,
					this.selectedCellCol);
			final boolean isCandidateVisible = candidatesForCell.contains(this.activeColorCandidateDigit);
			if (isCandidateVisible) {
				this.updateCandidateColorForSelectedCell();
			}
		}
	}

	private void updateCandidateColorForSelectedCell() {
		final SudokuPuzzleCell selectedCell = this.getSelectedCell();
		final int row = selectedCell.getRow();
		final int col = selectedCell.getCol();

		final ColorState currentColorState = this.candidateColorStates[col][row][this.activeColorCandidateDigit - 1];
		final Label candidateLabelForDigit = ViewController.getInstance().getSudokuPuzzleCell(row, col)
				.getCandidateLabelForDigit(this.activeColorCandidateDigit);

		final ColorState colorStateToApply = ColorState.getFromKeyCode(this.lastKeyCode, this.isShiftDown);

		final ObservableList<String> styleClass = candidateLabelForDigit.getStyleClass();
		if (colorStateToApply == currentColorState) {
			styleClass.remove(currentColorState.getCssClass());
			this.candidateColorStates[col][row][this.activeColorCandidateDigit - 1] = ColorState.NONE;
		} else if (currentColorState != ColorState.NONE) {
			styleClass.remove(currentColorState.getCssClass());
			styleClass.add(colorStateToApply.getCssClass());
			this.candidateColorStates[col][row][this.activeColorCandidateDigit - 1] = colorStateToApply;
		} else {
			styleClass.add(colorStateToApply.getCssClass());
			this.candidateColorStates[col][row][this.activeColorCandidateDigit - 1] = colorStateToApply;
		}
	}

}
