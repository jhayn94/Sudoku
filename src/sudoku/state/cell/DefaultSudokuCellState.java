package sudoku.state.cell;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.util.Strings;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import sudoku.view.puzzle.SudokuPuzzleCell;

/** This class corresponds to the default behavior of a sudoku cell. */
public class DefaultSudokuCellState {

	protected static final int NUM_CANDIDATES = 9;

	protected static final String UNFIXED_CELL_CSS_CLASS = "sudoku-unfixed-cell";

	protected static final String FIXED_CELL_CSS_CLASS = "sudoku-fixed-cell";

	protected static final String GIVEN_CELL_CSS_CLASS = "sudoku-given-cell";

	protected static final String SELECTED_CELL_CSS_CLASS = "sudoku-selected-cell";

	private static final List<String> CSS_CLASSES = Arrays.asList(UNFIXED_CELL_CSS_CLASS, FIXED_CELL_CSS_CLASS,
			GIVEN_CELL_CSS_CLASS);

	protected final boolean[] candidatesVisible;

	protected final boolean fixedDigitVisible;

	protected final SudokuPuzzleCell cell;

	public DefaultSudokuCellState(SudokuPuzzleCell cell) {
		this.cell = cell;
		this.candidatesVisible = new boolean[NUM_CANDIDATES];
		for (int i = 0; i < NUM_CANDIDATES; i++) {
			this.candidatesVisible[i] = true;
		}
		this.fixedDigitVisible = false;
	}

	public DefaultSudokuCellState(DefaultSudokuCellState lastState) {
		this.cell = lastState.cell;
		this.candidatesVisible = lastState.candidatesVisible;
		this.fixedDigitVisible = lastState.fixedDigitVisible;
		this.onEnter();
	}

	public SudokuPuzzleCell getCell() {
		return this.cell;
	}

	public EventHandler<KeyEvent> handleKeyPressed() {
		return event -> {
			final KeyCode code = event.getCode();
			if (code.isDigitKey()) {
				if (event.isControlDown()) {
					this.handleCtrlDigitPressed(code);
				} else {
					this.handleDigitPressed(code);
				}
			}
		};
	}

	public EventHandler<MouseEvent> handleClick() {
		return event -> {
			// The other cells are reset to not selected in CellChangeState.java. This is
			// because we don't have access to all the other cells from here; this class
			// only cares about the attached cell instance.
			this.cell.requestFocus();
			this.cell.setState(new SelectedCellState(this));
		};
	}

	protected void onEnter() {
		this.cell.getStyleClass().remove(SELECTED_CELL_CSS_CLASS);
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
		this.cell.setState(new UserFixedSudokuCellState(this));
	}

	protected void handleCtrlDigitPressed(final KeyCode code) {
		final int pressedDigitIndex = Integer.parseInt(code.getName()) - 1;
		final boolean isCandidateVisible = this.candidatesVisible[pressedDigitIndex];
		this.candidatesVisible[pressedDigitIndex] = !isCandidateVisible;
		this.cell.setCandidateVisible(pressedDigitIndex, !isCandidateVisible);
	}

	protected void handleDeletePressed() {
		this.getCell().setCandidatesVisible(true);
		this.getCell().setFixedDigit(Strings.EMPTY);
		this.getCell().setState(new DefaultSudokuCellState(this));
	}

}
