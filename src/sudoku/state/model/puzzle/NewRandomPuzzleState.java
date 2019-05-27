package sudoku.state.model.puzzle;

import java.util.List;

import org.apache.logging.log4j.util.Strings;

import sudoku.core.ViewController;
import sudoku.model.SudokuPuzzleValues;
import sudoku.state.ApplicationModelState;
import sudoku.view.puzzle.SudokuPuzzleCell;

/**
 * This class updates the state of the application when the user invokes a
 * "redo", either through the keyboard or a button press in the UI.
 */
public class NewRandomPuzzleState extends ApplicationModelState {

	private final String puzzleString;

	public NewRandomPuzzleState(final String puzzleString, final ApplicationModelState lastState) {
		super(lastState, false);
		this.puzzleString = puzzleString;
		this.applicationStateHistory.clearRedoStack();
		this.applicationStateHistory.clearUndoStack();
		this.updateUndoRedoButtons();
	}

	@Override
	public void onEnter() {
		this.resetAllFilters();
		this.updateFilterButtonStates(Strings.EMPTY);
		this.resetColorStates();
		this.sudokuPuzzleValues = new SudokuPuzzleValues(this.puzzleString);
		this.updateCells();
		// Must do this after because the cell values need to be finished before setting
		// candidates. Otherwise the doesCellSeeFixedDigit checks will not be correct.
		this.updateCandidates();
	}

	private void updateCandidates() {
		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
				final int givenCellDigit = this.sudokuPuzzleValues.getGivenCellDigit(row, col);
				final boolean isCellGiven = givenCellDigit != 0;
				this.setCandidateVisibility(row, col, sudokuPuzzleCell, isCellGiven);
			}
		}
	}

	private void updateCells() {
		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
				final int givenCellDigit = this.sudokuPuzzleValues.getGivenCellDigit(row, col);
				final boolean isCellGiven = givenCellDigit != 0;
				sudokuPuzzleCell.setFixedDigit(isCellGiven ? String.valueOf(givenCellDigit) : Strings.EMPTY);
				sudokuPuzzleCell.setCandidatesVisible(!isCellGiven);
				sudokuPuzzleCell.setCellGiven(isCellGiven);
				this.updateFixedCellTypeCssClass(sudokuPuzzleCell, isCellGiven ? GIVEN_CELL_CSS_CLASS : UNFIXED_CELL_CSS_CLASS);
			}
		}
	}

	private void setCandidateVisibility(final int row, final int col, final SudokuPuzzleCell sudokuPuzzleCell,
			final boolean isCellGiven) {
		if (!isCellGiven) {
			final List<Integer> candidateDigitsForCell = this.sudokuPuzzleValues.getCandidateDigitsForCell(row, col);
			for (int candidate = 1; candidate <= SudokuPuzzleValues.CELLS_PER_HOUSE; candidate++) {
				final boolean seesFixedDigit = this.doesCellSeeFixedDigit(row, col, candidate);
				if (seesFixedDigit) {
					candidateDigitsForCell.remove((Object) candidate);
				}
				sudokuPuzzleCell.setCandidateVisible(candidate, candidateDigitsForCell.contains(candidate) && !seesFixedDigit);
			}
		}
	}

}
