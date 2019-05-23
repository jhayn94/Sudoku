package sudoku.state;

import java.util.List;

import javafx.scene.input.KeyCode;
import sudoku.view.puzzle.SudokuPuzzleCell;

/**
 * This class corresponds to a sudoku cell which is set by the user. Candidates
 * cannot be toggled, but the fixed digit may be changed or deleted.
 */
public class SetDigitState extends ApplicationModelState {

	public SetDigitState(final KeyCode keyCode, final ApplicationModelState lastState) {
		super(lastState);
		this.lastKeyCode = keyCode;
	}

	@Override
	public void onEnter() {
		if (this.selectedCellRow != -1 && this.selectedCellCol != -1) {
			final SudokuPuzzleCell selectedCell = this.getSelectedCell();
			final int oldFixedDigit = selectedCell.getFixedDigit();

			// Update view.
			selectedCell.setCandidatesVisible(false);
			selectedCell.setFixedDigit(this.lastKeyCode.toString());
			this.updateCssClass(FIXED_CELL_CSS_CLASS);

			if (oldFixedDigit != -1) {
				this.addDigitAsCandidateToSeenCells(oldFixedDigit);
			}

			// Update model. Have to do model second to get new digit.
			final int fixedDigit = selectedCell.getFixedDigit();
			final List<SudokuPuzzleCell> visibleCells = this.getCellsSeenFrom(this.selectedCellRow,
					this.selectedCellCol);
			visibleCells.forEach(cell -> {
				cell.setCandidateVisible(fixedDigit, false);
				// Cast to object forces the list to remove by object reference instead
				// of index.
				this.puzzleModel.getCandidateDigitsForCell(cell.getRow(), cell.getCol()).remove((Object) fixedDigit);
			});
		}
	}

}
