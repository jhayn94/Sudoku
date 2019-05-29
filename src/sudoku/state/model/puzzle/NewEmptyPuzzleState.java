package sudoku.state.model.puzzle;

import org.apache.logging.log4j.util.Strings;

import sudoku.core.ViewController;
import sudoku.model.ApplicationSettings;
import sudoku.model.SudokuPuzzleValues;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.puzzle.SudokuPuzzleCell;

/**
 * This class updates the state of the application when the user presses the
 * "New Empty Puzzle" menu item.
 */
public class NewEmptyPuzzleState extends ApplicationModelState {

	public NewEmptyPuzzleState(final ApplicationModelState lastState) {
		super(lastState, false);
		this.applicationStateHistory.clearRedoStack();
		this.applicationStateHistory.clearUndoStack();
		this.updateUndoRedoButtons();
	}

	@Override
	public void onEnter() {
		this.sudokuPuzzleStyle.setActiveCellFilter(Strings.EMPTY);
		this.resetAllFilters();
		this.updateFilterButtonStates(Strings.EMPTY);
		this.resetColorStates();
		this.sudokuPuzzleValues = new SudokuPuzzleValues();
		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
				sudokuPuzzleCell.setFixedDigit(Strings.EMPTY);
				sudokuPuzzleCell.setCandidatesVisible(true);
				sudokuPuzzleCell.setCellGiven(false);
				this.updateFixedCellTypeCssClass(sudokuPuzzleCell, UNFIXED_CELL_CSS_CLASS);
				for (int candidate = 1; candidate <= SudokuPuzzleValues.CELLS_PER_HOUSE; candidate++) {
					sudokuPuzzleCell.setCandidateVisible(candidate, ApplicationSettings.getInstance().isAutoManageCandidates());
				}
			}
		}
	}

}
