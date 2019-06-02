package sudoku.state.model;

import sudoku.model.SudokuPuzzleValues;

/**
 * This class resets the application to the initial state for the current
 * puzzle.
 */
public class RestartPuzzleState extends ResetFromModelState {

	public RestartPuzzleState(final ApplicationModelState lastState) {
		super(lastState, false);
	}

	@Override
	public void onEnter() {
		this.applicationStateHistory.addToUndoStack(this.sudokuPuzzleValues);
		SudokuPuzzleValues puzzleStateForUndo = this.applicationStateHistory.getPuzzleStateForUndo();
		while (!this.applicationStateHistory.isUndoStackEmpty()) {
			puzzleStateForUndo = this.applicationStateHistory.getPuzzleStateForUndo();
		}
		this.sudokuPuzzleValues = puzzleStateForUndo;
		this.resetApplicationFromPuzzleState();
		this.updateAllPuzzleStatsForNewPuzzle();
		this.applicationStateHistory.clearRedoStack();
		this.applicationStateHistory.clearUndoStack();
		this.updateUndoRedoButtons();
	}

}
