package sudoku.model;

import java.util.Stack;

import sudoku.state.ApplicationModelState;

/**
 * This class tracks past states of the application, for undo purposes. It also
 * tracks undone actions so they could be redone later.
 */
public class ApplicationStateHistory {

	private final Stack<ApplicationModelState> undoStack;
	private final Stack<ApplicationModelState> redoStack;

	public ApplicationStateHistory() {
		this.undoStack = new Stack<>();
		this.redoStack = new Stack<>();
	}

	public void addToUndoStack(final ApplicationModelState newState) {
		this.undoStack.push(newState);
	}

	public void addToRedoStack(final ApplicationModelState newState) {
		this.redoStack.push(newState);
	}

	/** Gets the next state if an "undo" needs to occur. */
	public ApplicationModelState getStateForUndo() {
		return this.undoStack.pop();
	}

	/**
	 * Gets the next state if a "redo" needs to occur.
	 */
	public ApplicationModelState getStateForRedo() {
		return this.redoStack.pop();
	}

	/**
	 * Clears the redoStack. This should be done when the user undoes an action,
	 * then does something else. The undone states are no longer usable because we
	 * do not want to track a tree of states instead of just a stack.
	 */
	public void clearRedoStack() {
		this.redoStack.clear();
	}
}
