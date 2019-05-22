package sudoku.state.cell;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 * This class corresponds to a sudoku cell which is currently active, and can
 * receive input.
 */
public class SelectedCellState extends DefaultSudokuCellState {

	public SelectedCellState(DefaultSudokuCellState lastState) {
		super(lastState);
	}

	@Override
	protected void onEnter() {
		this.getCell().getStyleClass().add(SELECTED_CELL_CSS_CLASS);
	}

	@Override
	public EventHandler<MouseEvent> handleClick() {
		return event -> {
			this.cell.unselect();
		};
	}

}
