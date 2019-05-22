package sudoku.state.cell.active;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import sudoku.core.ModelController;
import sudoku.view.puzzle.SudokuPuzzleCell;

/**
 * A placeholder active state for a sudoku cell. There is no unique behavior,
 * but a parent class is needed for all other active states.
 */
public class DefaultCellActiveState {

	protected static final int NUM_CANDIDATES = 9;

	protected static final String SELECTED_CELL_CSS_CLASS = "sudoku-selected-cell";

	protected final SudokuPuzzleCell cell;

	protected DefaultCellActiveState lastState;

	public DefaultCellActiveState(SudokuPuzzleCell cell) {
		this.cell = cell;
	}

	public DefaultCellActiveState(DefaultCellActiveState lastState) {
		this.cell = lastState.cell;
		this.lastState = lastState;
	}

	public void handleKeyPressed(KeyEvent event) {
		final KeyCode code = event.getCode();
		if (code.isArrowKey()) {
			this.handleArrowPressed(code);
		}
	}

	public void handleClick(MouseEvent event) {
		// The other cells are reset to not selected in SelectionChangedState.java. This
		// is because we don't have access to all the other cells from here; this class
		// only cares about the attached cell instance.
		this.cell.setActiveState(new ActiveCellState(this));
	}

	public SudokuPuzzleCell getCell() {
		return this.cell;
	}

	public DefaultCellActiveState getLastState() {
		return this.lastState;
	}

	public void onEnter() {
		// Nothing to do.
	}

	protected void handleArrowPressed(final KeyCode code) {
		this.cell.unselect(false);
		ModelController.getInstance().transitionToSelectionChangedState(code, this);
	}

}
