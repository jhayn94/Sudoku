package sudoku.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import sudoku.core.ViewController;
import sudoku.factories.ModelFactory;
import sudoku.model.SudokuPuzzle;
import sudoku.view.puzzle.SudokuPuzzleCell;

/**
 * This class is a representation of the current state of the application model,
 * with methods to invoke when a state change occurs.
 */
public abstract class ApplicationModelState {

	protected static final String UNFIXED_CELL_CSS_CLASS = "sudoku-unfixed-cell";

	protected static final String FIXED_CELL_CSS_CLASS = "sudoku-fixed-cell";

	protected static final String GIVEN_CELL_CSS_CLASS = "sudoku-given-cell";

	protected static final String SELECTED_CELL_CSS_CLASS = "sudoku-selected-cell";

	private static final List<String> CSS_CLASSES = Arrays.asList(UNFIXED_CELL_CSS_CLASS, FIXED_CELL_CSS_CLASS,
			GIVEN_CELL_CSS_CLASS);

	// The active cell filter, or empty string for none.
	protected String activeCellFilter;

	// True if a filter should show the permitted cells for the active filter (if
	// any), false it should show the disallowed cells.
	protected final boolean filterAllowedCells;

	protected final SudokuPuzzle puzzleModel;

	protected int selectedCellRow;

	protected int selectedCellCol;

	protected KeyCode lastKeyCode;

	/** Constructor for the initialization of the application. */
	protected ApplicationModelState() {
		this.activeCellFilter = "";
		this.filterAllowedCells = false;
		this.puzzleModel = ModelFactory.getInstance().createSudokuPuzzle();
		this.lastKeyCode = null;
		ViewController.getInstance().getSudokuPuzzleView().requestFocus();
	}

	/** Constructor for state transitions. */
	protected ApplicationModelState(final ApplicationModelState lastState) {
		this.activeCellFilter = lastState.activeCellFilter;
		this.filterAllowedCells = lastState.filterAllowedCells;
		this.puzzleModel = lastState.puzzleModel;
		this.selectedCellRow = lastState.selectedCellRow;
		this.selectedCellCol = lastState.selectedCellCol;
		this.lastKeyCode = lastState.lastKeyCode;
		// Refocus grid so the keyboard actions always work.
		ViewController.getInstance().getSudokuPuzzleView().requestFocus();
	}

	public abstract void onEnter();

	protected List<SudokuPuzzleCell> getCellsSeenFrom(final int row, final int col) {
		final SudokuPuzzleCell cell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
		final List<SudokuPuzzleCell> cells = new ArrayList<>();
		for (int rowIndex = 0; rowIndex < SudokuPuzzle.NUMBER_OF_CELLS_PER_DIMENSION; rowIndex++) {
			if (rowIndex != row) {
				cells.add(ViewController.getInstance().getSudokuPuzzleCell(rowIndex, col));
			}
		}
		for (int colIndex = 0; colIndex < SudokuPuzzle.NUMBER_OF_CELLS_PER_DIMENSION; colIndex++) {
			if (colIndex != col) {
				cells.add(ViewController.getInstance().getSudokuPuzzleCell(row, colIndex));
			}
		}
		final int boxForCell = this.getBoxForCell(cell);
		this.getCellsInBox(cell, boxForCell).forEach(cellToAdd -> {
			if (!cellToAdd.equals(cell)) {
				cells.add(cellToAdd);
			}
		});
		return cells;
	}

	protected List<SudokuPuzzleCell> getCellsInBox(final SudokuPuzzleCell originalCell, final int box) {
		final List<SudokuPuzzleCell> cells = new ArrayList<>();
		// TODO - does this need to be more efficient?
		for (int rowIndex = 0; rowIndex < SudokuPuzzle.NUMBER_OF_CELLS_PER_DIMENSION; rowIndex++) {
			for (int colIndex = 0; colIndex < SudokuPuzzle.NUMBER_OF_CELLS_PER_DIMENSION; colIndex++) {
				final SudokuPuzzleCell cell = ViewController.getInstance().getSudokuPuzzleCell(rowIndex, colIndex);
				// TODO - should we skip this cell, or not?
				if (this.getBoxForCell(cell) == box && !originalCell.equals(cell)) {
					cells.add(cell);
				}
			}
		}
		return cells;
	}

	/**
	 * Gets the box number of the given cell. Returns -1 if row and col are
	 * outside of the puzzle dimensions.
	 */
	private int getBoxForCell(final SudokuPuzzleCell cell) {
		final int row = cell.getRow();
		final int col = cell.getCol();
		if (row <= 2 && col <= 2) {
			return 1;
		} else if (row <= 2 && col <= 5) {
			return 2;
		} else if (row <= 2 && col <= 8) {
			return 3;
		} else if (row <= 5 && col <= 2) {
			return 4;
		} else if (row <= 5 && col <= 5) {
			return 5;
		} else if (row <= 5 && col <= 8) {
			return 6;
		} else if (row <= 8 && col <= 2) {
			return 7;
		} else if (row <= 8 && col <= 5) {
			return 8;
		} else if (row <= 8 && col <= 8) {
			return 9;
		}
		return -1;
	}

	protected SudokuPuzzleCell getSelectedCell() {
		return ViewController.getInstance().getSudokuPuzzleCell(this.selectedCellRow, this.selectedCellCol);
	}

	protected void updateCssClass(final String newCssClass) {
		final ObservableList<String> styleClass = this.getSelectedCell().getStyleClass();
		CSS_CLASSES.forEach(styleClass::remove);
		styleClass.add(newCssClass);
	}
}
