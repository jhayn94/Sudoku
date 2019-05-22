package sudoku.state.cell.action;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * This class corresponds to a sudoku cell which is set by the user. Candidates
 * cannot be toggled, but the fixed digit may be changed or deleted.
 */
public class UserFixedCellActionState extends DefaultCellActionState {

	public UserFixedCellActionState(DefaultCellActionState lastState) {
		super(lastState);
	}

	@Override
	public void onEnter() {
		this.updateCssClass(FIXED_CELL_CSS_CLASS);
	}

	@Override
	public void handleKeyPressed(KeyEvent event) {
		final KeyCode code = event.getCode();
		if (code.isDigitKey() && !event.isControlDown()) {
			this.cell.setCandidatesVisible(false);
			this.cell.setFixedDigit(code.getName());
			this.cell.setActionState(new UserFixedCellActionState(this));
		} else if (KeyCode.DELETE == code) {
			this.handleDeletePressed();
		}
	}

}
