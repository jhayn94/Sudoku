package sudoku.core;

import sudoku.state.ApplicationModelState;
import sudoku.state.CellChangedState;
import sudoku.state.DefaultApplicationModelState;
import sudoku.state.FilterCandidatesState;
import sudoku.state.SelectionChangedState;
import sudoku.state.cell.action.DefaultCellActionState;
import sudoku.state.cell.active.DefaultCellActiveState;
import sudoku.state.window.ApplicationWindowState;
import sudoku.state.window.ClosedState;
import sudoku.state.window.MaximizedState;
import sudoku.state.window.MinimizedState;
import sudoku.state.window.RestoredState;
import sudoku.state.window.SoftRestoredState;

/** A controller class to facilitate state (model) changes. */
public class ModelController {

	private static ModelController modelStateControllerInstance;

	public static ModelController getInstance() {
		if (modelStateControllerInstance == null) {
			modelStateControllerInstance = new ModelController();
		}
		return modelStateControllerInstance;
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

	public void transitionToFilterCandidatesState(String filter) {
		this.applicationModelState = new FilterCandidatesState(this.applicationModelState, filter);
		this.applicationModelState.onEnter();
	}

	public void transitionToCellChangedState(DefaultCellActionState cellActionState) {
		this.applicationModelState = new CellChangedState(cellActionState, this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToSelectionChangedState(DefaultCellActiveState cellActiveState) {
		this.applicationModelState = new SelectionChangedState(cellActiveState, this.applicationModelState);
		this.applicationModelState.onEnter();
	}

}
