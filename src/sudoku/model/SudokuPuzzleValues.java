package sudoku.model;

import java.util.HashSet;
import java.util.Set;

import sudoku.factories.ModelFactory;

/**
 * This class represents all the underlying data for a sudoku puzzle. This
 * component is mostly concerned with the values. For other data, see
 * SudokuPuzzleStyle.
 *
 * Note: many public methods use row + col as parameters, but these are the
 * indices of each, not the traditional sudoku rows and columns.
 */
public class SudokuPuzzleValues {

	private static final String LEFT_BRACKET = "[";

	public static final int CELLS_PER_HOUSE = 9;

	private boolean hasGivens;

	private final Integer[][] givenCells;

	private final Integer[][] fixedCells;

	private final Set<Integer>[][] candidatesForCells;

	private int difficultyScore;

	@SuppressWarnings("unchecked")
	public SudokuPuzzleValues() {
		this.hasGivens = false;
		this.givenCells = new Integer[CELLS_PER_HOUSE][CELLS_PER_HOUSE];
		this.fixedCells = new Integer[CELLS_PER_HOUSE][CELLS_PER_HOUSE];
		this.candidatesForCells = new HashSet[CELLS_PER_HOUSE][CELLS_PER_HOUSE];
		for (int row = 0; row < CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < CELLS_PER_HOUSE; col++) {
				this.givenCells[col][row] = 0;
				this.fixedCells[col][row] = 0;
				this.candidatesForCells[col][row] = new HashSet<>();
				if (ApplicationSettings.getInstance().isAutoManageCandidates()) {
					for (int candidate = 0; candidate < CELLS_PER_HOUSE; candidate++) {
						this.candidatesForCells[col][row].add(candidate + 1);
					}
				}
			}
		}
	}

	public SudokuPuzzleValues(final String initialGivens) {
		this();
		this.hasGivens = true;
		if (initialGivens.contains(LEFT_BRACKET)) {
			this.updateCellAndCandidateValues(initialGivens, true);
		} else {
			this.updateCellValues(initialGivens, true);
		}
	}

	/** Gets the given digit at the given indices, or 0 if there is none. */
	public int getGivenCellDigit(final int row, final int col) {
		return this.givenCells[col][row];
	}

	/** Gets the fixed digit at the given indices, or 0 if there is none. */
	public int getFixedCellDigit(final int row, final int col) {
		return this.fixedCells[col][row];
	}

	public Set<Integer> getCandidateDigitsForCell(final int row, final int col) {
		return this.candidatesForCells[col][row];
	}

	public void setGivenCellDigit(final int row, final int col, final int given) {
		if (given != 0) {
			this.hasGivens = true;
			// A given cell is also fixed by definition.
			this.fixedCells[col][row] = given;
		}
		this.candidatesForCells[col][row].clear();
		this.givenCells[col][row] = given;
	}

	public void setCellFixedDigit(final int row, final int col, final int fixedDigit) {
		if (fixedDigit != 0) {
			this.candidatesForCells[col][row].clear();
		}
		this.fixedCells[col][row] = fixedDigit;
	}

	public void setCellCandidateDigits(final int row, final int col, final Set<Integer> candidates) {
		this.candidatesForCells[col][row] = candidates;
	}

	public void addCellCandidateDigit(final int row, final int col, final int candidate) {
		this.candidatesForCells[col][row].add(candidate);
	}

	public void removeCellCandidateDigit(final int row, final int col, final int candidate) {
		this.candidatesForCells[col][row].remove(candidate);
	}

	/** Creates and returns a deep copy of this. */
	@Override
	public SudokuPuzzleValues clone() {
		final SudokuPuzzleValues clone = ModelFactory.getInstance().createSudokuPuzzleValues();
		for (int row = 0; row < CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < CELLS_PER_HOUSE; col++) {
				clone.givenCells[col][row] = this.givenCells[col][row];
				clone.fixedCells[col][row] = this.fixedCells[col][row];
				clone.candidatesForCells[col][row] = new HashSet<>();
				clone.candidatesForCells[col][row].addAll(this.candidatesForCells[col][row]);
				clone.difficultyScore = this.difficultyScore;
				clone.hasGivens = this.hasGivens;
			}
		}
		return clone;
	}

	/**
	 * Returns the current state of the sudoku as string, where each digit is set if
	 * fixed in the puzzle.. 0 is used if no digit is set.
	 */
	public String toString(final boolean onlyGivens) {
		final StringBuilder sb = new StringBuilder();
		Integer[][] arrayToIterate;
		if (onlyGivens) {
			arrayToIterate = this.givenCells;
		} else {
			arrayToIterate = this.fixedCells;
		}
		for (int row = 0; row < CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < CELLS_PER_HOUSE; col++) {
				final Integer valueForCell = arrayToIterate[col][row];
				sb.append(String.valueOf(valueForCell));
			}
		}
		return sb.toString();
	}

	/**
	 * Updates cell and candidate values to match the values in the given
	 * puzzleString.
	 *
	 * @param puzzleString - a string, with 81 characters for the cells first. 1-9
	 *                     for digits, and 0 or . for non-filled cells. Then,
	 *                     additional candidates may be specified in the format of
	 *                     [rXcY=Z], where X and Y are a row and column, and Z is
	 *                     the possible candidates.
	 * @param setGivens    - true if givens should be set from the puzzleString,
	 *                     false otherwise.
	 */
	public void updateCellAndCandidateValues(final String puzzleString, final boolean setGivens) {
		this.updateCellValues(puzzleString, setGivens);
		final String[] candidateStrings = puzzleString.split("\\[");
		// Skip 0th index since it is just the regular cell givens.
		for (int index = 1; index < candidateStrings.length; index++) {
			final String candidatesStringForIndex = candidateStrings[index];
			final int row = Integer.valueOf(candidatesStringForIndex.charAt(1) - '0');
			final int col = Integer.valueOf(candidatesStringForIndex.charAt(3) - '0');
			final String candidates = candidatesStringForIndex.substring(5);
			for (int candidate = 1; candidate <= CELLS_PER_HOUSE; candidate++) {
				if (!candidates.contains(String.valueOf(candidate))) {
					this.candidatesForCells[col][row].remove(candidate);
				}
			}
		}
	}

	/**
	 * Updates cell values to match the values in the given puzzleString.
	 *
	 * @param puzzleString - a 81 digit string, with 1-9 for digits, and 0 or . for
	 *                     non-filled cells.
	 * @param setGivens    - true if givens should be set from the puzzleString,
	 *                     false otherwise.
	 */
	public void updateCellValues(final String puzzleString, final boolean setGivens) {
		for (int row = 0; row < CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < CELLS_PER_HOUSE; col++) {
				final int position = row * CELLS_PER_HOUSE + col;
				final char charAtPosition = puzzleString.charAt(position);
				if (Character.isDigit(charAtPosition)) {
					if (setGivens) {
						this.givenCells[col][row] = Integer.valueOf(charAtPosition) - '0';
					}
					this.fixedCells[col][row] = Integer.valueOf(charAtPosition) - '0';
					this.candidatesForCells[col][row].clear();
				}
			}
		}
	}

	public boolean hasGivens() {
		return this.hasGivens;
	}

	// Used to reset the puzzle to a non-playing state.
	public void setHasGivens(final boolean hasGivens) {
		this.hasGivens = hasGivens;
	}
}
