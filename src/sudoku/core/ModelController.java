package sudoku.core;

import sudoku.state.ApplicationModelState;
import sudoku.state.CellChangedState;
import sudoku.state.DefaultApplicationModelState;
import sudoku.state.FilterCandidatesState;
import sudoku.state.cell.DefaultSudokuCellState;
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
	}

	public void transitionToMinimizedState() {
		this.applicationWindowState = new MinimizedState(this.applicationWindowState);
	}

	public void transitionToMaximizedState() {
		this.applicationWindowState = new MaximizedState(this.applicationWindowState);
	}

	public void transitionToRestoredState() {
		this.applicationWindowState = new RestoredState(this.applicationWindowState);
	}

	public void transitionToSoftRestoredState() {
		this.applicationWindowState = new SoftRestoredState(this.applicationWindowState);
	}

	public void transitionToClosedState() {
		this.applicationWindowState = new ClosedState(this.applicationWindowState);
	}

	public void transitionToFilterCandidatesState(String filter) {
		this.applicationModelState = new FilterCandidatesState(this.applicationModelState, filter);
	}

	public void transitionToCellChangedState(int row, int col, DefaultSudokuCellState cellState) {
		this.applicationModelState = new CellChangedState(row, col, cellState, this.applicationModelState);
	}

}
