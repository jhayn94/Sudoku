package sudoku.model;

import java.util.Stack;

/**
 * This class tracks past states of the application, for undo purposes. It also
 * tracks undone actions so they could be redone later.
 */
public class ApplicationStateHistory {

	private final Stack<SudokuPuzzleValues> undoStack;
	private final Stack<SudokuPuzzleValues> redoStack;

	public ApplicationStateHistory() {
		this.undoStack = new Stack<>();
		this.redoStack = new Stack<>();
	}

	public void addToUndoStack(final SudokuPuzzleValues newPuzzleState) {
		this.undoStack.push(newPuzzleState.clone());
	}

	public void addToRedoStack(final SudokuPuzzleValues newPuzzleState) {
		this.redoStack.push(newPuzzleState.clone());
	}

	/** Gets the next puzzle state if an "undo" needs to occur. */
	public SudokuPuzzleValues getPuzzleStateForUndo() {
		return this.undoStack.pop();
	}

	/**
	 * Gets the next puzzle state if a "redo" needs to occur.
	 */
	public SudokuPuzzleValues getPuzzleStateForRedo() {
		return this.redoStack.pop();
	}

	/**
	 * Clears the redoStack. This should be done when the user undoes an action,
	 * then does something else. The undone states are no longer usable because we
	 * do not want to manage a tree of states instead of just a stack.
	 */
	public void clearRedoStack() {
		this.redoStack.clear();
	}

	/** Clears the undoStack. Mostly used for when the puzzle changes. */
	public void clearUndoStack() {
		this.undoStack.clear();
	}

	public boolean isUndoStackEmpty() {
		return this.undoStack.isEmpty();
	}

	public boolean isRedoStackEmpty() {
		return this.redoStack.isEmpty();
	}
}
