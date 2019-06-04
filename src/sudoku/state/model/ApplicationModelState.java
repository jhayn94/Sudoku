package sudoku.state.model;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.util.Strings;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import sudoku.SolutionStep;
import sudoku.core.HodokuFacade;
import sudoku.core.ViewController;
import sudoku.factories.ModelFactory;
import sudoku.model.ApplicationSettings;
import sudoku.model.ApplicationStateHistory;
import sudoku.model.SudokuPuzzleStyle;
import sudoku.model.SudokuPuzzleValues;
import sudoku.view.puzzle.SudokuPuzzleCell;
import sudoku.view.puzzle.SudokuPuzzleCellUtils;
import sudoku.view.sidebar.FilterButtonPane;
import sudoku.view.util.ColorUtils.ColorState;
import sudoku.view.util.Difficulty;
import sudoku.view.util.LabelConstants;
import sudoku.view.util.MouseMode;

/**
 * This class is a representation of the current state of the application model,
 * with methods to invoke when a state change occurs.
 *
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

	// Methods concerning the cells of the puzzle.

	protected SudokuPuzzleCell getSelectedCell() {
		return ViewController.getInstance().getSudokuPuzzleCell(this.sudokuPuzzleStyle.getSelectedCellRow(),
				this.sudokuPuzzleStyle.getSelectedCellCol());
	}

	// Methods that update the candidate labels / view components.

	/**
	 * Toggles the visibility of the given candidate active for the given cell.
	 */
	protected void toggleCandidateActiveForCell(final int pressedDigit, final SudokuPuzzleCell cell) {
		if (this.sudokuPuzzleValues.getFixedCellDigit(cell.getRow(), cell.getCol()) == 0) {
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
	 * Determines which candidates no longer are possible because of the number set
	 * in the given cell, and removes them from the model / view.
	 */
	protected void removeImpermissibleCandidates(final SudokuPuzzleCell cell) {
		for (int candidate = 1; candidate <= SudokuPuzzleValues.CELLS_PER_HOUSE; candidate++) {
			cell.setCandidateVisible(candidate, false);
		}
		final int fixedDigit = cell.getFixedDigit();
		final List<SudokuPuzzleCell> visibleCells = SudokuPuzzleCellUtils.getCellsSeenFrom(cell.getRow(), cell.getCol());
		visibleCells.forEach(otherCell -> {
			otherCell.setCandidateVisible(fixedDigit, false);
			// Cast to object forces the list to remove by object reference instead
			// of index.
			this.sudokuPuzzleValues.getCandidateDigitsForCell(otherCell.getRow(), otherCell.getCol())
					.remove((Object) fixedDigit);
		});
	}

	/**
	 * Adds the given digit to the cells seen by the selected cell, if no other
	 * fixed instances of that digit see the cell.
	 */
	protected void addDigitAsCandidateToSeenCells(final int fixedDigit) {
		final List<SudokuPuzzleCell> visibleCells = SudokuPuzzleCellUtils
				.getCellsSeenFrom(this.sudokuPuzzleStyle.getSelectedCellRow(), this.sudokuPuzzleStyle.getSelectedCellCol());
		visibleCells.forEach(cell -> {
			if (!SudokuPuzzleCellUtils.doesCellSeeFixedDigit(cell.getRow(), cell.getCol(), fixedDigit)) {
				cell.setCandidateVisible(fixedDigit, true);
				this.sudokuPuzzleValues.getCandidateDigitsForCell(cell.getRow(), cell.getCol()).add(fixedDigit);
			}
		});
	}

	/**
	 * Updates the candidates pane in the view to match the model. This should
	 * pretty much always be called after updateCells().
	 */
	protected void updateCandidates() {
		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
				final int givenCellDigit = this.sudokuPuzzleValues.getGivenCellDigit(row, col);
				final boolean isCellGiven = givenCellDigit != 0;
				this.setCandidateVisibility(row, col, sudokuPuzzleCell, isCellGiven);
			}
		}
	}

	// Puzzle Stat related methods.

	/**
	 * Updates the stats for the puzzle to match the currently set puzzle in
	 * this.sudokuPuzzleValues.
	 */
	protected void updateAllPuzzleStatsForNewPuzzle() {
		final Difficulty difficultyForPuzzle = HodokuFacade.getInstance().getDifficultyForPuzzle(this.sudokuPuzzleValues,
				false);
		ViewController.getInstance().getPuzzleStatsPane().getDifficultyTextField().setText(difficultyForPuzzle.getLabel());
		final int scoreForPuzzle = HodokuFacade.getInstance().getScoreForPuzzle(this.sudokuPuzzleValues, true);
		ViewController.getInstance().getPuzzleStatsPane().getRatingTextField().setText(String.valueOf(scoreForPuzzle));
		if (ApplicationSettings.getInstance().isShowPuzzleProgress()) {
			final int remainingScoreForPuzzle = HodokuFacade.getInstance().getScoreForPuzzle(this.sudokuPuzzleValues, false);
			ViewController.getInstance().getPuzzleStatsPane().getRemainingRatingTextField()
					.setText(String.valueOf(remainingScoreForPuzzle));
		} else {
			ViewController.getInstance().getPuzzleStatsPane().getRemainingRatingTextField().setText(Strings.EMPTY);
		}
	}

	/** Updates only the remaining score for the puzzle in the view. */
	protected void updateRemainingScoreForPuzzle() {
		// If the puzzle has no givens, skip this step for performance reasons for now.
		// the puzzle is trying to use brute force after each change, which makes things
		// quite slow.
		if (this.sudokuPuzzleValues.hasGivens()) {
			final TextField remainingRatingTextField = ViewController.getInstance().getPuzzleStatsPane()
					.getRemainingRatingTextField();
			if (ApplicationSettings.getInstance().isShowPuzzleProgress()) {
				final boolean isPuzzleValid = HodokuFacade.getInstance().isPuzzleValid(this.sudokuPuzzleValues);
				if (!isPuzzleValid) {
					remainingRatingTextField.setText(LabelConstants.INVALID_PUZZLE);
				} else {
					final int remainingScoreForPuzzle = HodokuFacade.getInstance().getScoreForPuzzle(this.sudokuPuzzleValues,
							false);
					remainingRatingTextField.setText(String.valueOf(remainingScoreForPuzzle));
				}
			} else {
				remainingRatingTextField.setText(Strings.EMPTY);
			}
		}
	}

	/**
	 * Clears the cell of CSS classes regarding the cell's fixed / unfixed / given
	 * state, then adds the given CSS class.
	 */
	protected void updateFixedCellTypeCssClass(final SudokuPuzzleCell cell, final String newFixedCellTypeCssClass) {
		final ObservableList<String> styleClass = cell.getStyleClass();
		FIXED_CELL_TYPE_CSS_CLASSES.forEach(styleClass::remove);
		styleClass.add(newFixedCellTypeCssClass);
	}

	// Filter state based methods.

	/** Clears the filter from every cell, if any. */
	protected void resetAllFilters() {
		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
				sudokuPuzzleCell.getStyleClass().remove(ACTIVE_FILTER_CELL_CSS_CLASS);
			}
		}
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
					if (this.sudokuPuzzleValues.getFixedCellDigit(row, col) == 0
							&& this.candidatesMatchFilter(predicate, candidates)) {
						final ObservableList<String> styleClass = sudokuPuzzleCell.getStyleClass();
						styleClass.add(ACTIVE_FILTER_CELL_CSS_CLASS);
					}
				}));
	}

	// Color state based methods.

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

	/**
	 * Given a cell and a color state, applies that color state to the cell for the
	 * active candidate.
	 */
	protected void setCandidateColorForCell(final int row, final int col, final ColorState colorStateToApply,
			final int candidate) {
		final ColorState currentColorState = this.sudokuPuzzleStyle.getCandidateColorState(row, col, candidate);
		final Label candidateLabelForDigit = ViewController.getInstance().getSudokuPuzzleCell(row, col)
				.getCandidateLabelForDigit(candidate);

		final ObservableList<String> styleClass = candidateLabelForDigit.getStyleClass();
		if (colorStateToApply == currentColorState) {
			styleClass.remove(currentColorState.getCssClass());
			this.sudokuPuzzleStyle.setCandidateColorState(row, col, candidate, ColorState.NONE);
		} else {
			if (currentColorState != ColorState.NONE) {
				styleClass.remove(currentColorState.getCssClass());
			}
			styleClass.add(colorStateToApply.getCssClass());
			this.sudokuPuzzleStyle.setCandidateColorState(row, col, candidate, colorStateToApply);
		}
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

	// Undo / redo based methods.

	protected void addPuzzleStateToUndoStack() {
		this.applicationStateHistory.addToUndoStack(this.sudokuPuzzleValues);
		this.applicationStateHistory.clearRedoStack();
		this.updateUndoRedoButtons();
	}

	/**
	 * Updates the undo and redo buttons / menu items based on their current sizes.
	 */
	protected void updateUndoRedoButtons() {
		final FilterButtonPane filterButtonPane = ViewController.getInstance().getFilterButtonPane();
		filterButtonPane.getUndoButton().setDisable(this.applicationStateHistory.isUndoStackEmpty());
		filterButtonPane.getRedoButton().setDisable(this.applicationStateHistory.isRedoStackEmpty());
		ViewController.getInstance().getUndoMenuItem().setDisable(this.applicationStateHistory.isUndoStackEmpty());
		ViewController.getInstance().getRedoMenuItem().setDisable(this.applicationStateHistory.isRedoStackEmpty());
	}

	private Boolean candidatesMatchFilter(final Function<List<Integer>, Boolean> predicate,
			final List<Integer> candidates) {
		return predicate.apply(candidates);
	}

	/**
	 * Sets the visibility for the given cell at the row / position based on the
	 * currently fixed cell values. Note that you should not call this in the same
	 * loops as updateCells(), since the values will be changing mid-iteration.
	 */
	private void setCandidateVisibility(final int row, final int col, final SudokuPuzzleCell sudokuPuzzleCell,
			final boolean isCellGiven) {
		if (!isCellGiven) {
			final List<Integer> candidateDigitsForCell = this.sudokuPuzzleValues.getCandidateDigitsForCell(row, col);
			for (int candidate = 1; candidate <= SudokuPuzzleValues.CELLS_PER_HOUSE; candidate++) {
				final boolean seesFixedDigit = SudokuPuzzleCellUtils.doesCellSeeFixedDigit(row, col, candidate);
				if (seesFixedDigit) {
					candidateDigitsForCell.remove((Object) candidate);
				}
				sudokuPuzzleCell.setCandidateVisible(candidate, candidateDigitsForCell.contains(candidate) && !seesFixedDigit
						&& ApplicationSettings.getInstance().isAutoManageCandidates());
			}
		}
	}

	/**
	 * Updates the CSS class of the given filter button based on the new active
	 * filter.
	 */
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

	/**
	 * Returns true if the new filter should result in the given button being marked
	 * as selected.
	 */
	private boolean shouldSetFilterButtonSelected(final String newCellFilter, final Button button) {
		final String buttonText = button.getText();
		return buttonText.equals(newCellFilter) && !buttonText.equals(this.sudokuPuzzleStyle.getActiveCellFilter());
	}

}
