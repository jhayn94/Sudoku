package sudoku.state.cell.active;

/**
 * This class corresponds to a sudoku cell's state when it is automatically
 * deactivated (i.e. when using arrow keys, the old cell becomes unselected).
 */
public class AutomaticallyInactiveCellState extends DefaultCellActiveState {

	public AutomaticallyInactiveCellState(DefaultCellActiveState lastState) {
		super(lastState);
	}

	@Override
	public void onEnter() {
		this.cell.getStyleClass().remove(SELECTED_CELL_CSS_CLASS);
	}

}
