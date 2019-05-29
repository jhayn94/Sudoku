package sudoku.model;

import org.apache.logging.log4j.util.Strings;

import javafx.scene.paint.Color;
import sudoku.view.util.ColorUtils;
import sudoku.view.util.ColorUtils.ColorState;

/**
 * This class represents all the underlying settings and style data for a sudoku
 * puzzle. For example, it tracks the selected cell index, the current color of
 * each cell + candidate, etc.
 *
 * This component is mostly concerned with the style component of the state. For
 * data related to the more mathematical / numeric values, see
 * SudokuPuzzleValues.
 */
public class SudokuPuzzleStyle {

	private static final int MIDDLE_CELL_INDEX = 4;

	// The active cell filter, or empty string for none.
	protected String activeCellFilter;

	// True if a filter should show the permitted cells for the active filter (if
	// any), false it should show the disallowed cells. Current unused...
	protected boolean filterAllowedCells;

	protected int selectedCellRow;

	protected int selectedCellCol;

	// Tracks the color of each cell in the sudoku puzzle. Note that the tan
	// selected color is not included here.
	protected ColorState[][] cellColorStates;

	// Tracks the color of each candidate in the sudoku puzzle.
	// 3 dimensions are columns, rows, and digits 1-9 in that order.
	protected ColorState[][][] candidateColorStates;

	// This is the candidate digit whose color should be toggled when the event
	// occurs. It is very difficult to allow the user to do any digit at once, so
	// a separate control changes this value.
	protected int activeCandidateDigit;

	private Color activeColor;

	public SudokuPuzzleStyle() {
		this.activeCandidateDigit = 1;
		this.activeCellFilter = Strings.EMPTY;
		this.activeColor = ColorUtils.getColors().get(0);
		this.filterAllowedCells = false;
		this.selectedCellRow = MIDDLE_CELL_INDEX;
		this.selectedCellCol = MIDDLE_CELL_INDEX;
		this.cellColorStates = new ColorState[SudokuPuzzleValues.CELLS_PER_HOUSE][SudokuPuzzleValues.CELLS_PER_HOUSE];
		this.candidateColorStates = new ColorState[SudokuPuzzleValues.CELLS_PER_HOUSE][SudokuPuzzleValues.CELLS_PER_HOUSE][SudokuPuzzleValues.CELLS_PER_HOUSE];
	}

	/**
	 * This method resets the coloring state of every cell and candidate to no
	 * color.
	 */
	public void resetColorStates() {
		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				this.cellColorStates[col][row] = ColorState.NONE;
				for (int candidate = 0; candidate < SudokuPuzzleValues.CELLS_PER_HOUSE; candidate++) {
					this.candidateColorStates[col][row][candidate] = ColorState.NONE;
				}
			}
		}
	}

	public void resetSelectedCellIndices() {
		this.selectedCellRow = -1;
		this.selectedCellCol = -1;
	}

	public String getActiveCellFilter() {
		return this.activeCellFilter;
	}

	public boolean isFilterAllowedCells() {
		return this.filterAllowedCells;
	}

	public int getSelectedCellRow() {
		return this.selectedCellRow;
	}

	public int getSelectedCellCol() {
		return this.selectedCellCol;
	}

	public Color getActiveColor() {
		return this.activeColor;
	}

	public ColorState getCellColorState(final int row, final int col) {
		return this.cellColorStates[col][row];
	}

	public ColorState getCandidateColorState(final int row, final int col, final int candidate) {
		return this.candidateColorStates[col][row][candidate - 1];
	}

	public int getActiveCandidateDigit() {
		return this.activeCandidateDigit;
	}

	public void setActiveCellFilter(final String activeCellFilter) {
		this.activeCellFilter = activeCellFilter;
	}

	public void setFilterAllowedCells(final boolean filterAllowedCells) {
		this.filterAllowedCells = filterAllowedCells;
	}

	public void setSelectedCellRow(final int selectedCellRow) {
		this.selectedCellRow = selectedCellRow;
	}

	public void setSelectedCellCol(final int selectedCellCol) {
		this.selectedCellCol = selectedCellCol;
	}

	public void setActiveColor(final Color newColor) {
		this.activeColor = newColor;
	}

	public void setCellColorState(final int row, final int col, final ColorState cellColorState) {
		this.cellColorStates[col][row] = cellColorState;
	}

	public void setCandidateColorState(final int row, final int col, final int candidate,
			final ColorState candidateColorState) {
		this.candidateColorStates[col][row][candidate - 1] = candidateColorState;
	}

	public void setActiveCandidateDigit(final int activeCandidateDigit) {
		this.activeCandidateDigit = activeCandidateDigit;
	}

}
