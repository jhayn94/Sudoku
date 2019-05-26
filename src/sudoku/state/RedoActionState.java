package sudoku.state;

import sudoku.model.SudokuPuzzleValues;

/**
 * This class updates the state of the application when the user invokes a
 * "redo", either through the keyboard or a button press in the UI.
 */
public class RedoActionState extends ResetFromModelState {

	public RedoActionState(final ApplicationModelState lastState) {
		super(lastState, false);
	}

	@Override
	public void onEnter() {
		if (!this.applicationStateHistory.isRedoStackEmpty()) {
			final SudokuPuzzleValues puzzleStateForRedo = this.applicationStateHistory.getPuzzleStateForRedo();
			this.applicationStateHistory.addToUndoStack(this.sudokuPuzzleValues);
			this.sudokuPuzzleValues = puzzleStateForRedo;
			this.resetApplicationFromPuzzleState();
			this.updateUndoRedoButtons();
		}
	}

}
