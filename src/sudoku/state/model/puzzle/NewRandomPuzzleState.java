package sudoku.state.model.puzzle;

import org.apache.logging.log4j.util.Strings;

import sudoku.model.SudokuPuzzleValues;
import sudoku.state.model.ApplicationModelState;
import sudoku.state.model.ResetFromModelState;

/**
 * This class updates the state of the application when the user presses the
 * "New Puzzle" menu item. This creates a new random puzzle with the current
 * puzzle settings.
 */
public class NewRandomPuzzleState extends ResetFromModelState {

	private final String puzzleString;

	public NewRandomPuzzleState(final String puzzleString, final ApplicationModelState lastState) {
		super(lastState, false);
		this.puzzleString = puzzleString;
	}

	@Override
	public void onEnter() {
		this.applicationStateHistory.clearRedoStack();
		this.applicationStateHistory.clearUndoStack();
		this.updateUndoRedoButtons();
		this.sudokuPuzzleStyle.setActiveCellFilter(Strings.EMPTY);
		this.resetAllFilters();
		this.updateFilterButtonStates(Strings.EMPTY);
		this.resetColorStates();
		this.sudokuPuzzleValues = new SudokuPuzzleValues(this.puzzleString);
		this.resetApplicationFromPuzzleState();
		this.updateAllPuzzleStatsForNewPuzzle();
	}

}
