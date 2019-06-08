package sudoku.view.puzzle;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import sudoku.core.ModelController;
import sudoku.factories.LayoutFactory;
import sudoku.model.SudokuPuzzleValues;
import sudoku.view.util.ColorUtils;

/**
 * This class corresponds to the view on the left side of the screen. It
 * contains all other view elements on this side of the application.
 */
public class SudokuPuzzleView extends GridPane {

	private static final String CSS_CLASS = "sudoku-transparent-pane";

	private static final String BOTTOM_CELL_CSS_CLASS = "sudoku-puzzle-cell-extra-bottom-border";

	private static final String TOP_CELL_CSS_CLASS = "sudoku-puzzle-cell-extra-top-border";

	private static final String LEFT_CELL_CSS_CLASS = "sudoku-puzzle-cell-extra-left-border";

	private static final String RIGHT_CELL_CSS_CLASS = "sudoku-puzzle-cell-extra-right-border";

	private static final String BOTTOM_RIGHT_CELL_CSS_CLASS = "sudoku-puzzle-cell-extra-bottom-right-border";

	private static final String TOP_RIGHT_CELL_CSS_CLASS = "sudoku-puzzle-cell-extra-top-right-border";

	private static final String BOTTOM_LEFT_CELL_CSS_CLASS = "sudoku-puzzle-cell-extra-bottom-left-border";

	private static final String TOP_LEFT_CELL_CSS_CLASS = "sudoku-puzzle-cell-extra-top-left-border";

	private static final int DEFAULT_WIDTH = SudokuPuzzleCell.CELL_WIDTH * SudokuPuzzleValues.CELLS_PER_HOUSE + 20;

	private static final int NUM_CELLS_TOTAL = SudokuPuzzleValues.CELLS_PER_HOUSE * SudokuPuzzleValues.CELLS_PER_HOUSE;

	public SudokuPuzzleView() {
		this.configure();
	}

	private void configure() {
		this.getStyleClass().add(CSS_CLASS);
		this.setPadding(new Insets(15));
		this.setMinWidth(DEFAULT_WIDTH);
		this.setMaxWidth(DEFAULT_WIDTH);
		this.setOnKeyPressed(this.onKeyPressed());
		this.createChildElements();
	}

	private void createChildElements() {
		this.createCellGridPane();
	}

	private void createCellGridPane() {
		for (int index = 1; index <= NUM_CELLS_TOTAL; index++) {
			// Integer division intentional!
			final int rowIndex = (index - 1) / 9;
			final int colIndex = (index - 1) % 9;
			final SudokuPuzzleCell sudokuPuzzleCell = LayoutFactory.getInstance().createSudokuPuzzleCell(colIndex, rowIndex);
			this.setBorderBasedOnBoxBoundaries(rowIndex, colIndex, sudokuPuzzleCell);
			this.add(sudokuPuzzleCell, colIndex, rowIndex);
		}
	}

	/**
	 * Adds a thicker border for cells which are on the edge of a box. The given
	 * index is a linearIndex for the whole puzzle (i.e. 0 to 80).
	 */
	private void setBorderBasedOnBoxBoundaries(final int rowIndex, final int colIndex,
			final SudokuPuzzleCell sudokuPuzzleCell) {

		final ObservableList<String> styleClass = sudokuPuzzleCell.getStyleClass();
		if (rowIndex % 3 == 0 && colIndex % 3 == 0) {
			styleClass.add(TOP_LEFT_CELL_CSS_CLASS);
		} else if (rowIndex % 3 == 0 && colIndex % 3 == 2) {
			styleClass.add(TOP_RIGHT_CELL_CSS_CLASS);
		} else if (rowIndex % 3 == 2 && colIndex % 3 == 0) {
			styleClass.add(BOTTOM_LEFT_CELL_CSS_CLASS);
		} else if (rowIndex % 3 == 2 && colIndex % 3 == 2) {
			styleClass.add(BOTTOM_RIGHT_CELL_CSS_CLASS);
		} else if (rowIndex % 3 == 0) {
			styleClass.add(TOP_CELL_CSS_CLASS);
		} else if (rowIndex % 3 == 2) {
			styleClass.add(BOTTOM_CELL_CSS_CLASS);
		} else if (colIndex % 3 == 0) {
			styleClass.add(LEFT_CELL_CSS_CLASS);
		} else if (colIndex % 3 == 2) {
			styleClass.add(RIGHT_CELL_CSS_CLASS);
		}
	}

	/**
	 * Handles all keyboard inputs for the application. The technical challenge with
	 * keyboard inputs is that the node must be focused to receive input, and only
	 * one node can be focused at once. This makes it impossible to listen on
	 * multiple nodes at once.
	 */
	private EventHandler<? super KeyEvent> onKeyPressed() {
		return event -> {
			final KeyCode keyCode = event.getCode();
			if (keyCode.isDigitKey() && !keyCode.getName().contains("0")) {
				this.onPressDigit(event, keyCode);
			} else if (keyCode.isLetterKey()) {
				this.onPressLetter(event, keyCode);
			} else if (keyCode.isArrowKey()) {
				ModelController.getInstance().transitionToArrowKeyboardInputState(keyCode);
			} else if (KeyCode.DELETE == keyCode) {
				ModelController.getInstance().transitionToRemoveDigitState(keyCode);
			} else if (KeyCode.MINUS == keyCode || KeyCode.EQUALS == keyCode) {
				ModelController.getInstance().transitionToActiveCandidateChangedState(keyCode);
			} else if (KeyCode.PERIOD == keyCode || KeyCode.COMMA == keyCode) {
				ModelController.getInstance().transitionToCycleActiveFilterState(keyCode.getName());
			} else if (KeyCode.ENTER == keyCode) {
				ModelController.getInstance().transitionToApplyHintState();
			} else if (KeyCode.BACK_SPACE == keyCode) {
				ModelController.getInstance().transitionToHideHintState();
			} else if (keyCode.isFunctionKey() && KeyCode.F11 != keyCode && KeyCode.F12 != keyCode) {
				final String formattedKeyCodeName = keyCode.getName().replace("F", "").replace("10", "X|Y");
				ModelController.getInstance().transitionToApplyFilterState(formattedKeyCodeName);
			}
		};
	}

	private void onPressDigit(final KeyEvent event, final KeyCode keyCode) {
		if (event.isAltDown()) {
			ModelController.getInstance().transitionToActiveCandidateChangedState(keyCode);
		} else if (event.isControlDown()) {
			ModelController.getInstance().transitionToToggleCandidateVisibleState(keyCode);
		} else {
			ModelController.getInstance().transitionToSetDigitState(keyCode);
		}
	}

	private void onPressColoringKey(final KeyEvent event, final KeyCode keyCode) {
		if (event.isControlDown()) {
			ModelController.getInstance().transitionToToggleCandidateColorState(keyCode, event.isShiftDown());
		} else {
			ModelController.getInstance().transitionToToggleCellColorState(keyCode, event.isShiftDown());
		}
	}

	private void onPressLetter(final KeyEvent event, final KeyCode keyCode) {
		if (KeyCode.R == keyCode) {
			ModelController.getInstance().transitionToResetAllColorsState();
		} else if (KeyCode.M == keyCode && event.isControlDown()) {
			ModelController.getInstance().transitionToShowContextMenuState();
		} else if (ColorUtils.getApplyColorKeyCodes().contains(keyCode)) {
			this.onPressColoringKey(event, keyCode);
		}
	}

}
