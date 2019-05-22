package sudoku.state.cell.action;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.util.Strings;

import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sudoku.view.puzzle.SudokuPuzzleCell;

/** This class corresponds to the default behavior of a sudoku cell. */
public class DefaultCellActionState {

	protected static final int NUM_CANDIDATES = 9;

	protected static final String UNFIXED_CELL_CSS_CLASS = "sudoku-unfixed-cell";

	protected static final String FIXED_CELL_CSS_CLASS = "sudoku-fixed-cell";

	protected static final String GIVEN_CELL_CSS_CLASS = "sudoku-given-cell";

	private static final List<String> CSS_CLASSES = Arrays.asList(UNFIXED_CELL_CSS_CLASS, FIXED_CELL_CSS_CLASS,
			GIVEN_CELL_CSS_CLASS);

	protected final boolean[] candidatesVisible;

	protected final boolean fixedDigitVisible;

	protected final SudokuPuzzleCell cell;

	private DefaultCellActionState lastState;

	public DefaultCellActionState(SudokuPuzzleCell cell) {
		this.cell = cell;
		this.candidatesVisible = new boolean[NUM_CANDIDATES];
		for (int i = 0; i < NUM_CANDIDATES; i++) {
			this.candidatesVisible[i] = true;
		}
		this.fixedDigitVisible = false;
	}

	public DefaultCellActionState(DefaultCellActionState lastState) {
		this.cell = lastState.cell;
		this.candidatesVisible = lastState.candidatesVisible;
		this.fixedDigitVisible = lastState.fixedDigitVisible;
		this.lastState = lastState;
	}

	public SudokuPuzzleCell getCell() {
		return this.cell;
	}

	public DefaultCellActionState getLastState() {
		return this.lastState;
	}

	public void handleKeyPressed(KeyEvent event) {
		final KeyCode code = event.getCode();
		if (code.isDigitKey()) {
			if (event.isControlDown()) {
				this.handleCtrlDigitPressed(code);
			} else {
				this.handleDigitPressed(code);
			}
		}
	}

	public void onEnter() {
		this.updateCssClass(UNFIXED_CELL_CSS_CLASS);
	}

	protected void updateCssClass(String newCssClass) {
		final ObservableList<String> styleClass = this.cell.getStyleClass();
		CSS_CLASSES.forEach(styleClass::remove);
		styleClass.add(newCssClass);
	}

	protected void handleDigitPressed(final KeyCode code) {
		this.cell.setCandidatesVisible(false);
		this.cell.setFixedDigit(code.getName());
		this.cell.setActionState(new UserFixedCellActionState(this));
	}

	protected void handleCtrlDigitPressed(final KeyCode code) {
		final int pressedDigitIndex = Integer.parseInt(code.getName()) - 1;
		final boolean isCandidateVisible = this.candidatesVisible[pressedDigitIndex];
		this.candidatesVisible[pressedDigitIndex] = !isCandidateVisible;
		this.cell.setCandidateVisible(pressedDigitIndex, !isCandidateVisible);
	}

	protected void handleDeletePressed() {
		this.cell.setCandidatesVisible(true);
		this.cell.setFixedDigit(Strings.EMPTY);
		this.cell.setActionState(new DefaultCellActionState(this));
	}

}
