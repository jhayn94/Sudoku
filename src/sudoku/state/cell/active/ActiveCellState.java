package sudoku.state.cell.active;

import javafx.scene.input.MouseEvent;

/**
 * This class corresponds to a sudoku cell which is currently active, and can
 * receive input.
 */
public class ActiveCellState extends DefaultCellActiveState {

	public ActiveCellState(DefaultCellActiveState lastState) {
		super(lastState);
	}

	@Override
	public void onEnter() {
		this.cell.requestFocus();
		this.getCell().getStyleClass().add(SELECTED_CELL_CSS_CLASS);
	}

	@Override
	public void handleClick(MouseEvent event) {
		this.cell.unselect(true);
	}

}
