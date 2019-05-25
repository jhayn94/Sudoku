package sudoku.state;

import sudoku.model.SudokuPuzzleValues;

/**
 * This class updates the state of the application when the user invokes an
 * "undo", either through the keyboard or a button press in the UI.
 */
public class UndoActionState extends ResetFromModelState {

	public UndoActionState(final ApplicationModelState lastState) {
		super(lastState, false);
	}

	@Override
	public void onEnter() {
		super.onEnter();
		if (!this.applicationStateHistory.isUndoStackEmpty()) {
			final SudokuPuzzleValues puzzleStateForUndo = this.applicationStateHistory.getPuzzleStateForUndo();
			this.applicationStateHistory.addToRedoStack(this.sudokuPuzzleValues);
			this.sudokuPuzzleValues = puzzleStateForUndo;
			this.resetApplicationFromPuzzleState();
			this.updateUndoRedoButtons();
		}
	}

}
