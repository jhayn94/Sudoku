package sudoku.state.cell;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

/**
 * This class corresponds to a sudoku cell which is a given and cannot be
 * changed.
 */
public class GivenSudokuCellState extends DefaultSudokuCellState {

	public GivenSudokuCellState(DefaultSudokuCellState lastState) {
		super(lastState);
	}

	@Override
	protected void onEnter() {
		this.cell.getStyleClass().remove(SELECTED_CELL_CSS_CLASS);
		this.updateCssClass(GIVEN_CELL_CSS_CLASS);
		this.cell.setCellGiven(true);
	}

	@Override
	public EventHandler<KeyEvent> handleKeyPress() {
		return event -> {
			// Nothing to do, for now.
			// TODO - add handling for adding a color shade to this cell.
		};
	}
}
