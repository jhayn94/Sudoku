package sudoku.model;

import java.util.ArrayList;
import java.util.List;

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

	private final Integer[][] givenCells;

	private final Integer[][] fixedCells;

	private final List<Integer>[][] candidatesForCells;

	private int difficultyScore;

	@SuppressWarnings("unchecked")
	public SudokuPuzzleValues() {
		this.givenCells = new Integer[CELLS_PER_HOUSE][CELLS_PER_HOUSE];
		this.fixedCells = new Integer[CELLS_PER_HOUSE][CELLS_PER_HOUSE];
		this.candidatesForCells = new ArrayList[CELLS_PER_HOUSE][CELLS_PER_HOUSE];
		for (int row = 0; row < CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < CELLS_PER_HOUSE; col++) {
				this.givenCells[col][row] = 0;
				this.fixedCells[col][row] = 0;
				this.candidatesForCells[col][row] = new ArrayList<>();
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
		if (initialGivens.contains(LEFT_BRACKET)) {
			this.initializePuzzleWithProgress(initialGivens);
		} else {
			this.initializePuzzleWithNoProgress(initialGivens);
		}
	}

	private void initializePuzzleWithProgress(final String initialGivens) {
		this.initializePuzzleWithNoProgress(initialGivens);
		final String[] candidateStrings = initialGivens.split("\\[");
		// Skip 0th index since it is just the regular cell givens.
		for (int index = 1; index < candidateStrings.length; index++) {
			final String candidatesStringForIndex = candidateStrings[index];
			final int row = Integer.valueOf(candidatesStringForIndex.charAt(1) - '0');
			final int col = Integer.valueOf(candidatesStringForIndex.charAt(3) - '0');
			final String candidates = candidatesStringForIndex.substring(5);
			for (int candidate = 1; candidate <= CELLS_PER_HOUSE; candidate++) {
				if (!candidates.contains(String.valueOf(candidate))) {
					this.candidatesForCells[col][row].remove((Object) candidate);
				}
			}
		}
	}

	private void initializePuzzleWithNoProgress(final String initialGivens) {
		for (int row = 0; row < CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < CELLS_PER_HOUSE; col++) {
				final int position = row * CELLS_PER_HOUSE + col;
				final char charAtPosition = initialGivens.charAt(position);
				if (Character.isDigit(charAtPosition)) {
					this.givenCells[col][row] = Integer.valueOf(charAtPosition) - '0';
					this.fixedCells[col][row] = Integer.valueOf(charAtPosition) - '0';
				}
			}
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

	public List<Integer> getCandidateDigitsForCell(final int row, final int col) {
		return this.candidatesForCells[col][row];
	}

	public void setGivenCellDigit(final int row, final int col, final int given) {
		this.candidatesForCells[col][row].clear();
		this.givenCells[col][row] = given;
		// A given cell is also fixed by definition.
		this.fixedCells[col][row] = given;
	}

	public void setCellFixedDigit(final int row, final int col, final int fixedDigit) {
		this.candidatesForCells[col][row].clear();
		this.fixedCells[col][row] = fixedDigit;
	}

	public void setCellCandidateDigits(final int row, final int col, final List<Integer> candidates) {
		this.candidatesForCells[col][row] = candidates;
	}

	public void addCellCandidateDigit(final int row, final int col, final int candidate) {
		this.candidatesForCells[col][row].add(candidate);
	}

	public void removeCellCandidateDigit(final int row, final int col, final int candidate) {
		this.candidatesForCells[col][row].remove((Object) candidate);
	}

	/**
	 * Gets the box number of the given cell. Returns -1 if row and col are outside
	 * of the puzzle dimensions.
	 */
	public int getBoxForCell(final int row, final int col) {
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

	/** Creates and returns a deep copy of this. */
	@Override
	public SudokuPuzzleValues clone() {
		final SudokuPuzzleValues clone = ModelFactory.getInstance().createSudokuPuzzleValues();
		for (int row = 0; row < CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < CELLS_PER_HOUSE; col++) {
				clone.givenCells[col][row] = this.givenCells[col][row];
				clone.fixedCells[col][row] = this.fixedCells[col][row];
				clone.candidatesForCells[col][row] = new ArrayList<>();
				clone.candidatesForCells[col][row].addAll(this.candidatesForCells[col][row]);
				clone.difficultyScore = this.difficultyScore;
			}
		}
		return clone;
	}

	/**
	 * Returns the current state of the sudoku as string, where each digit is set if
	 * fixed in the puzzle.. 0 is used if no digit is set.
	 */
	public String getSudoku() {
		final StringBuilder result = new StringBuilder();
		for (int row = 0; row < CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < CELLS_PER_HOUSE; col++) {
				result.append(this.fixedCells[col][row]);
			}
		}
		return result.toString();
	}

	/** Returns true iff this puzzle is solved. */
	public boolean isSolved() {
		for (int row = 0; row < CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < CELLS_PER_HOUSE; col++) {
				if (this.fixedCells[col][row] == 0) {
					return false;
				}
			}
		}
		return true;
	}

	public int getDifficultyScore() {
		return this.difficultyScore;
	}

	public void setDifficultyScore(final int score) {
		this.difficultyScore = score;
	}

	public int getNumberOfUnsolvedCells() {
		int unsolvedCellsCount = 0;
		for (int row = 0; row < CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < CELLS_PER_HOUSE; col++) {
				if (this.fixedCells[col][row] == 0) {
					unsolvedCellsCount++;
				}
			}
		}
		return unsolvedCellsCount;
	}

	public String getStringRepresentation(final boolean onlyGivens) {
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
}
