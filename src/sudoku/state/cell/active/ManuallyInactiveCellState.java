package sudoku.state.cell.active;

/**
 * This class corresponds to a sudoku cell's state when it is manually
 * deactivated (i.e. by mouse click).
 */
public class ManuallyInactiveCellState extends DefaultCellActiveState {

	public ManuallyInactiveCellState(DefaultCellActiveState lastState) {
		super(lastState);
	}

	@Override
	public void onEnter() {
		this.cell.getStyleClass().remove(SELECTED_CELL_CSS_CLASS);
	}

}
