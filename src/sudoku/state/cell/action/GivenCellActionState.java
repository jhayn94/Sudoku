package sudoku.state.cell.action;

import javafx.scene.input.KeyEvent;

/**
 * This class corresponds to a sudoku cell which is a given and cannot be
 * changed.
 */
public class GivenCellActionState extends DefaultCellActionState {

	public GivenCellActionState(DefaultCellActionState lastState) {
		super(lastState);
	}

	@Override
	protected void onEnter() {
		this.updateCssClass(GIVEN_CELL_CSS_CLASS);
		this.cell.setCellGiven(true);
	}

	@Override
	public void handleKeyPressed(KeyEvent event) {
		// Nothing to do, for now.
		// TODO - add handling for adding a color shade to this cell.
	}
}
