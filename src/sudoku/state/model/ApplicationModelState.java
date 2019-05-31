package sudoku.state.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import sudoku.SolutionStep;
import sudoku.core.ViewController;
import sudoku.factories.ModelFactory;
import sudoku.model.ApplicationStateHistory;
import sudoku.model.SudokuPuzzleStyle;
import sudoku.model.SudokuPuzzleValues;
import sudoku.view.puzzle.SudokuPuzzleCell;
import sudoku.view.sidebar.FilterButtonPane;
import sudoku.view.util.ColorUtils.ColorState;
import sudoku.view.util.MouseMode;

/**
 * This class is a representation of the current state of the application model,
 * with methods to invoke when a state change occurs.
 *
 * TODO - this class is getting too big, maybe some of the methods should be
 * moved out into other classes.
 */
public class ApplicationModelState {

	protected static final String SUDOKU_BUTTON_SELECTED_CSS_CLASS = "sudoku-button-selected";

	protected static final String SUDOKU_BUTTON_UNSELECTED_CSS_CLASS = "sudoku-button-unselected";

	protected static final String ACTIVE_FILTER_CELL_CSS_CLASS = "sudoku-active-filter-cell";

	protected static final String UNFIXED_CELL_CSS_CLASS = "sudoku-unfixed-cell";

	protected static final String FIXED_CELL_CSS_CLASS = "sudoku-fixed-cell";

	protected static final String GIVEN_CELL_CSS_CLASS = "sudoku-given-cell";

	private static final List<String> FIXED_CELL_TYPE_CSS_CLASSES = Arrays.asList(UNFIXED_CELL_CSS_CLASS,
			FIXED_CELL_CSS_CLASS, GIVEN_CELL_CSS_CLASS);

	protected SudokuPuzzleValues sudokuPuzzleValues;

	protected SudokuPuzzleStyle sudokuPuzzleStyle;

	protected KeyCode lastKeyCode;

	protected MouseMode mouseMode;

	protected SolutionStep displayedHint;

	protected ApplicationStateHistory applicationStateHistory;

	/** Constructor for the initialization of the application. */
	protected ApplicationModelState() {
		this.sudokuPuzzleValues = ModelFactory.getInstance().createSudokuPuzzleValues();
		this.sudokuPuzzleStyle = ModelFactory.getInstance().createSudokuPuzzleStyle();
		this.lastKeyCode = null;
		this.displayedHint = null;
		this.applicationStateHistory = ModelFactory.getInstance().createApplicationStateHistory();
		this.mouseMode = MouseMode.SELECT_CELLS;
		this.resetColorStates();
		this.updateUndoRedoButtons();
		ViewController.getInstance().getSudokuPuzzleView().requestFocus();
	}

	/**
	 * Constructor for state transitions. Use addToHistory = true to allow the state
	 * change to be reverted (undo).
	 */
	protected ApplicationModelState(final ApplicationModelState lastState, final boolean addToHistory) {
		this.sudokuPuzzleValues = lastState.sudokuPuzzleValues;
		this.sudokuPuzzleStyle = lastState.sudokuPuzzleStyle;
		this.lastKeyCode = lastState.lastKeyCode;
		this.displayedHint = lastState.displayedHint;
		this.mouseMode = lastState.mouseMode;
		this.applicationStateHistory = lastState.applicationStateHistory;
		if (addToHistory) {
			this.addPuzzleStateToUndoStack();
		}
		// Some states are invoked by clicks. So, refocus grid is called to make
		// keyboard actions always work (see SudokuPuzzleView for more notes on why
		// this
		// is done).
		ViewController.getInstance().getSudokuPuzzleView().requestFocus();
	}

	public void onEnter() {
		// Nothing to do.
	}

	protected void addPuzzleStateToUndoStack() {
		this.applicationStateHistory.addToUndoStack(this.sudokuPuzzleValues);
		this.applicationStateHistory.clearRedoStack();
		this.updateUndoRedoButtons();
	}

	/**
	 * This method resets the coloring state of every cell and candidate label to no
	 * color.
	 */
	protected void resetColorStates() {
		this.sudokuPuzzleStyle.resetColorStates();
		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
				final ObservableList<String> styleClass = sudokuPuzzleCell.getStyleClass();
				final List<String> colorCssClasses = Arrays.asList(ColorState.values()).stream().map(ColorState::getCssClass)
						.collect(Collectors.toList());
				colorCssClasses.forEach(styleClass::remove);
				for (int candidate = 0; candidate < SudokuPuzzleValues.CELLS_PER_HOUSE; candidate++) {
					final Label candidateLabelForDigit = sudokuPuzzleCell.getCandidateLabelForDigit(candidate + 1);
					final ObservableList<String> candidateLabelStyleClass = candidateLabelForDigit.getStyleClass();
					colorCssClasses.forEach(candidateLabelStyleClass::remove);
				}
			}
		}
	}

	/** Clears the filter from every cell, if any. */
	protected void resetAllFilters() {
		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
				sudokuPuzzleCell.getStyleClass().remove(ACTIVE_FILTER_CELL_CSS_CLASS);
			}
		}
	}

	protected SudokuPuzzleCell getSelectedCell() {
		return ViewController.getInstance().getSudokuPuzzleCell(this.sudokuPuzzleStyle.getSelectedCellRow(),
				this.sudokuPuzzleStyle.getSelectedCellCol());
	}

	/** Gets a list of cells seen from the given row and column. */
	protected List<SudokuPuzzleCell> getCellsSeenFrom(final int row, final int col) {
		final SudokuPuzzleCell cell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
		final List<SudokuPuzzleCell> cells = new ArrayList<>();
		for (int rowIndex = 0; rowIndex < SudokuPuzzleValues.CELLS_PER_HOUSE; rowIndex++) {
			if (rowIndex != row) {
				cells.add(ViewController.getInstance().getSudokuPuzzleCell(rowIndex, col));
			}
		}
		for (int colIndex = 0; colIndex < SudokuPuzzleValues.CELLS_PER_HOUSE; colIndex++) {
			if (colIndex != col) {
				cells.add(ViewController.getInstance().getSudokuPuzzleCell(row, colIndex));
			}
		}
		final int boxForCell = this.sudokuPuzzleValues.getBoxForCell(cell.getRow(), cell.getCol());
		this.getCellsInBox(boxForCell).forEach(cells::add);
		return cells.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * Returns a list of cells in the given box (1 - 9). Any other inputs will
	 * return an empty list.
	 */
	protected List<SudokuPuzzleCell> getCellsInBox(final int box) {
		final List<SudokuPuzzleCell> cells = new ArrayList<>();
		for (int rowIndex = 0; rowIndex < SudokuPuzzleValues.CELLS_PER_HOUSE; rowIndex++) {
			for (int colIndex = 0; colIndex < SudokuPuzzleValues.CELLS_PER_HOUSE; colIndex++) {
				final SudokuPuzzleCell cell = ViewController.getInstance().getSudokuPuzzleCell(rowIndex, colIndex);
				if (this.sudokuPuzzleValues.getBoxForCell(rowIndex, colIndex) == box) {
					cells.add(cell);
				}
			}
		}
		return cells;
	}

	/**
	 * Returns true iff the cell at the given row and column see a fixed cell with
	 * the given digit.
	 */
	protected boolean doesCellSeeFixedDigit(final int row, final int col, final int fixedDigit) {
		final List<SudokuPuzzleCell> visibleCells = this.getCellsSeenFrom(row, col);
		final long numDigitInstancesSeen = visibleCells.stream().filter(cell -> cell.getFixedDigit() == fixedDigit).count();
		return numDigitInstancesSeen > 0;
	}

	/**
	 * Refreshes the active filter by clearing it, and applying it again. This is
	 * necessary when other model components change, which may cause the filter to
	 * yield a different result.
	 */
	protected void reapplyActiveFilter() {
		if (!this.sudokuPuzzleStyle.getActiveCellFilter().isEmpty()) {
			this.resetAllFilters();
			this.applyActiveFilter();
		}
	}

	/**
	 * Updates each of the filter buttons with the correct background, indicating if
	 * it is active or not.
	 *
	 * @param newCellFilter - the new filter to apply, for which a corresponding
	 *                      button needs to be set as active.
	 */
	protected void updateFilterButtonStates(final String newCellFilter) {
		final FilterButtonPane filterButtonPane = ViewController.getInstance().getFilterButtonPane();
		final List<Button> filterButtons = filterButtonPane.getFilterButtons();
		filterButtons.forEach(button -> this.updateFilterButton(newCellFilter, button));
	}

	protected void toggleCandidateActiveForCell(final int pressedDigit, final SudokuPuzzleCell cell) {
		if (!cell.isCellFixed()) {
			final List<Integer> candidatesForCell = this.sudokuPuzzleValues.getCandidateDigitsForCell(cell.getRow(),
					cell.getCol());
			final boolean isCandidateVisible = candidatesForCell.contains(pressedDigit);
			cell.setCandidateVisible(pressedDigit, !isCandidateVisible);
			if (isCandidateVisible) {
				candidatesForCell.remove((Object) pressedDigit);
			} else {
				candidatesForCell.add(pressedDigit);
			}
		}
		this.reapplyActiveFilter();
	}

	/**
	 * Adds a CSS class to every applicable cell that satisfies the
	 * activeCellFilter. These cells get the CSS class
	 * 'ACTIVE_FILTER_CELL_CSS_CLASS'.
	 */
	protected void applyActiveFilter() {
		// Define a predicate to use to determine if the cell should be shaded. If
		// the activeFilter's length is 1, it is a single digit filter. Otherwise,
		// it is a bivalue cell filter. This line initializes a function to filter
		// the appropriate cells for either case. It is done in 1 line because it
		// must be final to use in a lambda function below.
		final Function<List<Integer>, Boolean> predicate = this.sudokuPuzzleStyle.getActiveCellFilter().length() > 1
				? candidates -> candidates.size() == 2
				: candidates -> candidates.contains(Integer.parseInt(this.sudokuPuzzleStyle.getActiveCellFilter()));

		IntStream.rangeClosed(0, SudokuPuzzleValues.CELLS_PER_HOUSE - 1)
				.forEach(row -> IntStream.rangeClosed(0, SudokuPuzzleValues.CELLS_PER_HOUSE - 1).forEach(col -> {
					final List<Integer> candidates = this.sudokuPuzzleValues.getCandidateDigitsForCell(row, col);
					final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
					if (!sudokuPuzzleCell.isCellFixed() && this.candidatesMatchFilter(predicate, candidates)) {
						final ObservableList<String> styleClass = sudokuPuzzleCell.getStyleClass();
						styleClass.add(ACTIVE_FILTER_CELL_CSS_CLASS);
					}
				}));
	}

	/**
	 * Given a cell and a color state, applies that color state to the cell for the
	 * active candidate.
	 */
	protected void updateCandidateColorForCell(final SudokuPuzzleCell cell, final ColorState colorStateToApply) {
		final int row = cell.getRow();
		final int col = cell.getCol();
		final int activeColorCandidateDigit = this.sudokuPuzzleStyle.getActiveCandidateDigit();
		final ColorState currentColorState = this.sudokuPuzzleStyle.getCandidateColorState(row, col,
				activeColorCandidateDigit);
		final Label candidateLabelForDigit = ViewController.getInstance().getSudokuPuzzleCell(row, col)
				.getCandidateLabelForDigit(activeColorCandidateDigit);

		final ObservableList<String> styleClass = candidateLabelForDigit.getStyleClass();
		if (colorStateToApply == currentColorState) {
			styleClass.remove(currentColorState.getCssClass());
			this.sudokuPuzzleStyle.setCandidateColorState(row, col, activeColorCandidateDigit, ColorState.NONE);
		} else {
			if (currentColorState != ColorState.NONE) {
				styleClass.remove(currentColorState.getCssClass());
			}
			styleClass.add(colorStateToApply.getCssClass());
			this.sudokuPuzzleStyle.setCandidateColorState(row, col, activeColorCandidateDigit, colorStateToApply);
		}
	}

	/**
	 * Determines which candidates no longer are possible because of the set number,
	 * and removes them from the model / view.
	 */
	protected void removeImpermissibleCandidates(final SudokuPuzzleCell cell) {
		for (int candidate = 1; candidate <= SudokuPuzzleValues.CELLS_PER_HOUSE; candidate++) {
			cell.setCandidateVisible(candidate, false);
		}
		final int fixedDigit = cell.getFixedDigit();
		final List<SudokuPuzzleCell> visibleCells = this.getCellsSeenFrom(cell.getRow(), cell.getCol());
		visibleCells.forEach(otherCell -> {
			otherCell.setCandidateVisible(fixedDigit, false);
			// Cast to object forces the list to remove by object reference instead
			// of index.
			this.sudokuPuzzleValues.getCandidateDigitsForCell(otherCell.getRow(), otherCell.getCol())
					.remove((Object) fixedDigit);
		});
	}

	/**
	 * Given a row, col and a color state, applies that color state to the
	 * registered cell in the row and col.
	 */
	protected void setColorStateForCell(final int row, final int col, final ColorState colorStateToApply) {
		final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
		final ColorState currentColorState = this.sudokuPuzzleStyle.getCellColorState(row, col);
		final ObservableList<String> styleClass = sudokuPuzzleCell.getStyleClass();
		if (colorStateToApply == currentColorState) {
			styleClass.remove(currentColorState.getCssClass());
			this.sudokuPuzzleStyle.setCellColorState(row, col, ColorState.NONE);
		} else {
			if (currentColorState != ColorState.NONE) {
				styleClass.remove(currentColorState.getCssClass());
			}
			styleClass.add(colorStateToApply.getCssClass());
			this.sudokuPuzzleStyle.setCellColorState(row, col, colorStateToApply);
		}
	}

	protected void updateUndoRedoButtons() {
		final FilterButtonPane filterButtonPane = ViewController.getInstance().getFilterButtonPane();
		filterButtonPane.getUndoButton().setDisable(this.applicationStateHistory.isUndoStackEmpty());
		filterButtonPane.getRedoButton().setDisable(this.applicationStateHistory.isRedoStackEmpty());
		ViewController.getInstance().getUndoMenuItem().setDisable(this.applicationStateHistory.isUndoStackEmpty());
		ViewController.getInstance().getRedoMenuItem().setDisable(this.applicationStateHistory.isRedoStackEmpty());
	}

	/**
	 * Adds the given digit to the cells seen by the selected cell, if no other
	 * fixed instances of that digit see the cell.
	 */
	protected void addDigitAsCandidateToSeenCells(final int fixedDigit) {
		final List<SudokuPuzzleCell> visibleCells = this.getCellsSeenFrom(this.sudokuPuzzleStyle.getSelectedCellRow(),
				this.sudokuPuzzleStyle.getSelectedCellCol());
		visibleCells.forEach(cell -> {
			if (!this.doesCellSeeFixedDigit(cell.getRow(), cell.getCol(), fixedDigit)) {
				cell.setCandidateVisible(fixedDigit, true);
				this.sudokuPuzzleValues.getCandidateDigitsForCell(cell.getRow(), cell.getCol()).add(fixedDigit);
			}
		});
	}

	protected void updateFixedCellTypeCssClass(final SudokuPuzzleCell cell, final String newFixedCellTypeCssClass) {
		final ObservableList<String> styleClass = cell.getStyleClass();
		FIXED_CELL_TYPE_CSS_CLASSES.forEach(styleClass::remove);
		styleClass.add(newFixedCellTypeCssClass);
	}

	private Boolean candidatesMatchFilter(final Function<List<Integer>, Boolean> predicate,
			final List<Integer> candidates) {
		return predicate.apply(candidates);
	}

	private void updateFilterButton(final String newCellFilter, final Button button) {
		final ObservableList<String> styleClass = button.getStyleClass();
		// Since we iterate over every button every time, the classes are fully
		// cleared to avoid duplicate classes. This is easier than tracking when to
		// remove each CSS class separately.
		styleClass.remove(SUDOKU_BUTTON_SELECTED_CSS_CLASS);
		styleClass.remove(SUDOKU_BUTTON_UNSELECTED_CSS_CLASS);
		if (!this.shouldSetFilterButtonSelected(newCellFilter, button)) {
			styleClass.add(SUDOKU_BUTTON_UNSELECTED_CSS_CLASS);
		} else {
			styleClass.add(SUDOKU_BUTTON_SELECTED_CSS_CLASS);
		}
	}

	private boolean shouldSetFilterButtonSelected(final String newCellFilter, final Button button) {
		final String buttonText = button.getText();
		return buttonText.equals(newCellFilter) && !buttonText.equals(this.sudokuPuzzleStyle.getActiveCellFilter());
	}

}
