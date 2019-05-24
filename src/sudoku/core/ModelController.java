package sudoku.core;

import javafx.scene.input.KeyCode;
import sudoku.state.ApplicationModelState;
import sudoku.state.ArrowKeyboardInputState;
import sudoku.state.ClickedCellState;
import sudoku.state.DefaultApplicationModelState;
import sudoku.state.FilterCandidatesState;
import sudoku.state.RemoveDigitState;
import sudoku.state.SetDigitState;
import sudoku.state.ToggleCandidateColorState;
import sudoku.state.ToggleCandidateVisibleState;
import sudoku.state.window.ApplicationWindowState;
import sudoku.state.window.ClosedState;
import sudoku.state.window.MaximizedState;
import sudoku.state.window.MinimizedState;
import sudoku.state.window.RestoredState;
import sudoku.state.window.SoftRestoredState;

/** A controller class to facilitate state (model) changes. */
public class ModelController {

	private static ModelController instance;

	public static ModelController getInstance() {
		if (ModelController.instance == null) {
			ModelController.instance = new ModelController();
		}
		return ModelController.instance;
	}

	/**
	 * Contains info about the state of the model (i.e. the data behind the result
	 * view and the input elements).
	 */
	private ApplicationModelState applicationModelState;

	/**
	 * Contains info about the state of the window (i.e. maximized, window bounds,
	 * etc.).
	 */
	private ApplicationWindowState applicationWindowState;

	private ModelController() {
		this.applicationModelState = new DefaultApplicationModelState();
		this.applicationWindowState = new MaximizedState();
		this.applicationModelState.onEnter();
		this.applicationWindowState.onEnter();
	}

	public void transitionToMinimizedState() {
		this.applicationWindowState = new MinimizedState(this.applicationWindowState);
		this.applicationWindowState.onEnter();
	}

	public void transitionToMaximizedState() {
		this.applicationWindowState = new MaximizedState(this.applicationWindowState);
		this.applicationWindowState.onEnter();
	}

	public void transitionToRestoredState() {
		this.applicationWindowState = new RestoredState(this.applicationWindowState);
		this.applicationWindowState.onEnter();
	}

	public void transitionToSoftRestoredState() {
		this.applicationWindowState = new SoftRestoredState(this.applicationWindowState);
		this.applicationWindowState.onEnter();
	}

	public void transitionToClosedState() {
		this.applicationWindowState = new ClosedState(this.applicationWindowState);
		this.applicationWindowState.onEnter();
	}

	public void transitionToFilterCandidatesState(final String filter) {
		this.applicationModelState = new FilterCandidatesState(this.applicationModelState, filter);
		this.applicationModelState.onEnter();
	}

	public void transitionToClickedCellState(final int row, final int col) {
		this.applicationModelState = new ClickedCellState(row, col, this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToArrowKeyboardInputState(final KeyCode keyCode) {
		this.applicationModelState = new ArrowKeyboardInputState(keyCode, this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToSetDigitState(final KeyCode keyCode) {
		this.applicationModelState = new SetDigitState(keyCode, this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToRemoveDigitState(final KeyCode keyCode) {
		this.applicationModelState = new RemoveDigitState(keyCode, this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToToggleCandidateVisibleState(final KeyCode keyCode) {
		this.applicationModelState = new ToggleCandidateVisibleState(keyCode, this.applicationModelState);
		this.applicationModelState.onEnter();

	}

	public void transitionToToggleCandidateColorState(final KeyCode keyCode, final boolean isShiftDown) {
		this.applicationModelState = new ToggleCandidateColorState(keyCode, isShiftDown, this.applicationModelState);
		this.applicationModelState.onEnter();
	}
}
