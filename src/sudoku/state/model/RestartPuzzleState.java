package sudoku.state.model;

import org.apache.logging.log4j.util.Strings;

import sudoku.core.ViewController;
import sudoku.model.SudokuPuzzleValues;
import sudoku.view.hint.HintTextArea;

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
		ViewController.getInstance().getRootPane().removeAllAnnotations();
		this.applicationStateHistory.addToUndoStack(this.sudokuPuzzleValues);
		SudokuPuzzleValues puzzleStateForUndo = this.applicationStateHistory.getPuzzleStateForUndo();
		while (!this.applicationStateHistory.isUndoStackEmpty()) {
			puzzleStateForUndo = this.applicationStateHistory.getPuzzleStateForUndo();
		}
		this.sudokuPuzzleValues = puzzleStateForUndo;
		this.resetApplicationFromPuzzleState();
		this.resetAllColorStates();
		this.updateAllPuzzleStatsForNewPuzzle();
		this.applicationStateHistory.clearRedoStack();
		this.applicationStateHistory.clearUndoStack();
		this.updateUndoRedoButtons();

		final HintTextArea hintTextArea = ViewController.getInstance().getHintTextArea();
		hintTextArea.getHintTextArea().setText(Strings.EMPTY);
	}

}
