package sudoku.state.model;

/**
 * This class contains methods to reset the application according to the current
 * sudoku puzzle set. This is used mainly as a parent class for undo + redo
 * states.
 */
public abstract class ResetFromModelState extends ApplicationModelState {

	protected ResetFromModelState(final ApplicationModelState lastState, final boolean addToHistory) {
		super(lastState, false);
	}

	/**
	 * Uses the value of SudokuPuzzleValues in this.sudokuPuzzleValues, and resets
	 * the rest of the model + view to match it. This is used for redo, undo and
	 * restart.
	 */
	protected void resetApplicationFromPuzzleState() {
//		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
//			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
//				final int fixedCellDigit = this.sudokuPuzzleValues.getFixedCellDigit(row, col);
//				final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
//				if (fixedCellDigit != 0) {
//					sudokuPuzzleCell.setCandidatesVisible(false);
//					sudokuPuzzleCell.setFixedDigit(String.valueOf(fixedCellDigit));
//					final int givenCellDigit = this.sudokuPuzzleValues.getGivenCellDigit(row, col);
//					this.sudokuPuzzleValues.getCandidateDigitsForCell(row, col).clear();
//					this.updateFixedCellTypeCssClass(sudokuPuzzleCell,
//							givenCellDigit == 0 ? FIXED_CELL_CSS_CLASS : GIVEN_CELL_CSS_CLASS);
//				} else {
//					this.sudokuPuzzleValues.setCellFixedDigit(row, col, 0);
//					sudokuPuzzleCell.setCandidatesVisible(true);
//					sudokuPuzzleCell.setFixedDigit(Strings.EMPTY);
//
//					this.updateFixedCellTypeCssClass(sudokuPuzzleCell, UNFIXED_CELL_CSS_CLASS);
//				}
//
//			}
//		}
//
//		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
//			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
//				final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
//				final List<Integer> candidateDigitsForCell = this.sudokuPuzzleValues.getCandidateDigitsForCell(row, col);
//				for (int candidate = 1; candidate <= SudokuPuzzleValues.CELLS_PER_HOUSE; candidate++) {
//					if (this.doesCellSeeFixedDigit(row, col, candidate)) {
//						candidateDigitsForCell.remove((Object) candidate);
//					}
//					sudokuPuzzleCell.setCandidateVisible(candidate, candidateDigitsForCell.contains(candidate));
//				}
//			}
//		}

		this.updateCells();
		// Must do this after because the cell values need to be finished before
		// setting candidates. Otherwise the doesCellSeeFixedDigit checks will not be
		// correct.
		this.updateCandidates();
		this.reapplyActiveFilter();
	}
}
