package sudoku.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import sudoku.core.ViewController;
import sudoku.factories.ModelFactory;
import sudoku.model.SudokuPuzzle;
import sudoku.view.puzzle.SudokuPuzzleCell;
import sudoku.view.util.ColorUtils.ColorState;

/**
 * This class is a representation of the current state of the application model,
 * with methods to invoke when a state change occurs.
 */
public abstract class ApplicationModelState {

	protected static final String ACTIVE_FILTER_CELL_CSS_CLASS = "sudoku-active-filter-cell";

	protected static final String UNFIXED_CELL_CSS_CLASS = "sudoku-unfixed-cell";

	protected static final String FIXED_CELL_CSS_CLASS = "sudoku-fixed-cell";

	protected static final String GIVEN_CELL_CSS_CLASS = "sudoku-given-cell";

	private static final List<String> FIXED_CELL_TYPE_CSS_CLASSES = Arrays.asList(UNFIXED_CELL_CSS_CLASS,
			FIXED_CELL_CSS_CLASS, GIVEN_CELL_CSS_CLASS);

	// The active cell filter, or empty string for none.
	protected String activeCellFilter;

	// True if a filter should show the permitted cells for the active filter (if
	// any), false it should show the disallowed cells.
	protected final boolean filterAllowedCells;

	protected final SudokuPuzzle puzzleModel;

	protected int selectedCellRow;

	protected int selectedCellCol;

	protected KeyCode lastKeyCode;

	// Tracks the color of each cell in the sudoku puzzle. Note that the tan
	// selected color is not included here.
	protected ColorState[][] cellColorStates;

	// Tracks the color of each candidate in the sudoku puzzle.
	// 3 dimensions are columns, rows, and digits 1-9 in that order.
	protected ColorState[][][] candidateColorStates;

	// This is the candidate digit whose color should be toggled when the event
	// occurs. It is very difficult to allow the user to do any digit at once, so
	// a
	// separate control changes this value.
	protected int activeColorCandidateDigit;

	/** Constructor for the initialization of the application. */
	protected ApplicationModelState() {
		this.activeCellFilter = "";
		this.filterAllowedCells = false;
		this.puzzleModel = ModelFactory.getInstance().createSudokuPuzzle();
		this.lastKeyCode = null;
		this.activeColorCandidateDigit = 1;
		this.cellColorStates = new ColorState[SudokuPuzzle.CELLS_PER_HOUSE][SudokuPuzzle.CELLS_PER_HOUSE];
		this.candidateColorStates = new ColorState[SudokuPuzzle.CELLS_PER_HOUSE][SudokuPuzzle.CELLS_PER_HOUSE][SudokuPuzzle.CELLS_PER_HOUSE];
		this.resetColorStates();
		ViewController.getInstance().getSudokuPuzzleView().requestFocus();
	}

	/** Constructor for state transitions. */
	protected ApplicationModelState(final ApplicationModelState lastState) {
		this.activeCellFilter = lastState.activeCellFilter;
		this.filterAllowedCells = lastState.filterAllowedCells;
		this.puzzleModel = lastState.puzzleModel;
		this.selectedCellRow = lastState.selectedCellRow;
		this.selectedCellCol = lastState.selectedCellCol;
		this.activeColorCandidateDigit = lastState.activeColorCandidateDigit;
		this.lastKeyCode = lastState.lastKeyCode;
		this.cellColorStates = lastState.cellColorStates;
		this.candidateColorStates = lastState.candidateColorStates;
		// Refocus grid so the keyboard actions always work.
		ViewController.getInstance().getSudokuPuzzleView().requestFocus();
	}

	public abstract void onEnter();

	/**
	 * This method resets the coloring state of every cell and candidate label to
	 * no color.
	 */
	protected void resetColorStates() {
		for (int row = 0; row < SudokuPuzzle.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzle.CELLS_PER_HOUSE; col++) {
				this.cellColorStates[col][row] = ColorState.NONE;
				final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
				final ObservableList<String> styleClass = sudokuPuzzleCell.getStyleClass();
				final List<String> colorCssClasses = Arrays.asList(ColorState.values()).stream().map(ColorState::getCssClass)
						.collect(Collectors.toList());
				colorCssClasses.forEach(styleClass::remove);
				for (int candidate = 0; candidate < SudokuPuzzle.CELLS_PER_HOUSE; candidate++) {
					this.candidateColorStates[col][row][candidate] = ColorState.NONE;
					final Label candidateLabelForDigit = sudokuPuzzleCell.getCandidateLabelForDigit(candidate + 1);
					final ObservableList<String> candidateLabelStyleClass = candidateLabelForDigit.getStyleClass();
					colorCssClasses.forEach(candidateLabelStyleClass::remove);
				}
			}
		}
	}

	protected void resetAllFilters() {
		for (int row = 0; row < SudokuPuzzle.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzle.CELLS_PER_HOUSE; col++) {
				final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
				sudokuPuzzleCell.getStyleClass().remove(ACTIVE_FILTER_CELL_CSS_CLASS);
			}
		}
	}

	protected SudokuPuzzleCell getSelectedCell() {
		return ViewController.getInstance().getSudokuPuzzleCell(this.selectedCellRow, this.selectedCellCol);
	}

	protected List<SudokuPuzzleCell> getCellsSeenFrom(final int row, final int col) {
		final SudokuPuzzleCell cell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
		final List<SudokuPuzzleCell> cells = new ArrayList<>();
		for (int rowIndex = 0; rowIndex < SudokuPuzzle.CELLS_PER_HOUSE; rowIndex++) {
			if (rowIndex != row) {
				cells.add(ViewController.getInstance().getSudokuPuzzleCell(rowIndex, col));
			}
		}
		for (int colIndex = 0; colIndex < SudokuPuzzle.CELLS_PER_HOUSE; colIndex++) {
			if (colIndex != col) {
				cells.add(ViewController.getInstance().getSudokuPuzzleCell(row, colIndex));
			}
		}
		final int boxForCell = this.getBoxForCell(cell);
		this.getCellsInBox(boxForCell).forEach(cells::add);
		return cells.stream().distinct().collect(Collectors.toList());
	}

	protected List<SudokuPuzzleCell> getCellsInBox(final int box) {
		final List<SudokuPuzzleCell> cells = new ArrayList<>();
		for (int rowIndex = 0; rowIndex < SudokuPuzzle.CELLS_PER_HOUSE; rowIndex++) {
			for (int colIndex = 0; colIndex < SudokuPuzzle.CELLS_PER_HOUSE; colIndex++) {
				final SudokuPuzzleCell cell = ViewController.getInstance().getSudokuPuzzleCell(rowIndex, colIndex);
				if (this.getBoxForCell(cell) == box) {
					cells.add(cell);
				}
			}
		}
		return cells;
	}

	protected boolean doesCellSeeFixedDigit(final int row, final int col, final int fixedDigit) {
		final List<SudokuPuzzleCell> visibleCells = this.getCellsSeenFrom(row, col);
		final long numDigitInstancesSeen = visibleCells.stream().filter(cell -> cell.getFixedDigit() == fixedDigit).count();
		return numDigitInstancesSeen > 0;
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

	protected void reapplyActiveFilter() {
		if (!this.activeCellFilter.isEmpty()) {
			this.resetAllFilters();
			this.applyActiveFilter();
		}
	}

	protected void applyActiveFilter() {
		// Define a predicate to use to determine if the cell should be shaded. If
		// the activeFilter's length is 1, it is a single digit filter. Otherwise,
		// it is a bivalue cell filter. This line initializes a function to filter
		// the appropriate cells for either case. It is done in 1 line because it
		// must be final to use in a lambda function below.
		final Function<List<Integer>, Boolean> predicate = this.activeCellFilter.length() > 1
				? candidates -> candidates.size() == 2
				: candidates -> candidates.contains(Integer.parseInt(this.activeCellFilter));

		IntStream.rangeClosed(0, SudokuPuzzle.CELLS_PER_HOUSE - 1)
				.forEach(row -> IntStream.rangeClosed(0, SudokuPuzzle.CELLS_PER_HOUSE - 1).forEach(col -> {
					final List<Integer> candidates = this.puzzleModel.getCandidateDigitsForCell(row, col);
					final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
					if (!sudokuPuzzleCell.isCellFixed() && this.candidatesMatchFilter(predicate, candidates)) {
						final ObservableList<String> styleClass = sudokuPuzzleCell.getStyleClass();
						styleClass.add(ACTIVE_FILTER_CELL_CSS_CLASS);
					}
				}));
	}

	/**
	 * Adds the given digit to the cells seen by the selected cell, if no other
	 * fixed instances of that digit see the cell.
	 */
	protected void addDigitAsCandidateToSeenCells(final int fixedDigit) {
		final List<SudokuPuzzleCell> visibleCells = this.getCellsSeenFrom(this.selectedCellRow, this.selectedCellCol);
		visibleCells.forEach(cell -> {
			if (!this.doesCellSeeFixedDigit(cell.getRow(), cell.getCol(), fixedDigit)) {
				cell.setCandidateVisible(fixedDigit, true);
				this.puzzleModel.getCandidateDigitsForCell(cell.getRow(), cell.getCol()).add(fixedDigit);
			}
		});
	}

	protected void updateFixedCellTypeCssClass(final String newFixedCellTypeCssClass) {
		final ObservableList<String> styleClass = this.getSelectedCell().getStyleClass();
		FIXED_CELL_TYPE_CSS_CLASSES.forEach(styleClass::remove);
		styleClass.add(newFixedCellTypeCssClass);
	}

	private Boolean candidatesMatchFilter(final Function<List<Integer>, Boolean> predicate,
			final List<Integer> candidates) {
		return predicate.apply(candidates);
	}

}
